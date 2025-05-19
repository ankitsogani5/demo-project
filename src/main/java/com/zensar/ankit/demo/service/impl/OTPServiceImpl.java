package com.zensar.ankit.demo.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.zensar.ankit.demo.dto.OTPResponseDTO;
import com.zensar.ankit.demo.entity.OTP;
import com.zensar.ankit.demo.exception.OTPAttemptsExceededException;
import com.zensar.ankit.demo.exception.OTPDeliveryFailureException;
import com.zensar.ankit.demo.exception.OTPException;
import com.zensar.ankit.demo.exception.OTPExpiredException;
import com.zensar.ankit.demo.exception.OTPGenerationFailedException;
import com.zensar.ankit.demo.exception.OTPInvalidException;
import com.zensar.ankit.demo.exception.OTPNotFoundException;
import com.zensar.ankit.demo.exception.OTPRateLimitedException;
import com.zensar.ankit.demo.repository.OTPRepository;
import com.zensar.ankit.demo.service.OTPService;
import com.zensar.ankit.demo.service.SMSService;

/**
 * Implementation of the OTPService interface for generating, verifying, and managing
 * One-Time Passwords (OTPs) for mobile number verification.
 * 
 * This service is responsible for:
 * 1. Generating secure random OTP codes
 * 2. Sending OTPs via SMS
 * 3. Verifying submitted OTP codes
 * 4. Managing OTP expiration
 * 5. Tracking verification attempts
 * 6. Implementing rate limiting to prevent abuse
 */
@Service
public class OTPServiceImpl implements OTPService {

    private static final Logger logger = LoggerFactory.getLogger(OTPServiceImpl.class);
    
    private static final String OTP_CHARS = "0123456789";
    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    
    private final SecureRandom secureRandom;
    
    @Autowired
    private OTPRepository otpRepository;
    
    @Autowired
    private SMSService smsService;
    
    // Configuration properties with default values
    @Value("${otp.length:6}")
    private int otpLength;
    
    @Value("${otp.expiration.seconds:600}")
    private int otpExpirationSeconds;
    
    @Value("${otp.max-attempts:3}")
    private int maxVerificationAttempts;
    
    @Value("${otp.rate-limit.max-requests:3}")
    private int maxRequestsPerWindow;
    
    @Value("${otp.rate-limit.window-hours:1}")
    private int rateLimitWindowHours;
    
    @Value("${otp.cooling-period.seconds:300}")
    private int coolingPeriodSeconds;
    
    // In-memory cache for OTP rate limiting
    private final LoadingCache<String, Integer> otpRequestCountCache;
    
    // In-memory cache for OTP verification
    private final LoadingCache<String, OTP> otpCache;
    
    /**
     * Constructor that initializes the SecureRandom instance and caches.
     * 
     * @throws OTPGenerationFailedException if the SecureRandom algorithm is not available
     */
    public OTPServiceImpl() {
        try {
            // Initialize SecureRandom with a strong algorithm
            this.secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
            
            // Initialize rate limiting cache
            this.otpRequestCountCache = CacheBuilder.newBuilder()
                    .maximumSize(10000)
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    .build(new CacheLoader<String, Integer>() {
                        @Override
                        public Integer load(String key) {
                            return 0;
                        }
                    });
            
            // Initialize OTP cache for faster verification
            this.otpCache = CacheBuilder.newBuilder()
                    .maximumSize(10000)
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    .build(new CacheLoader<String, OTP>() {
                        @Override
                        public OTP load(String mobileNumber) {
                            // Load from database if not in cache
                            Optional<OTP> latestOtp = otpRepository.findLatestValidOtpForMobile(
                                    mobileNumber, LocalDateTime.now());
                            return latestOtp.orElseThrow(() -> 
                                new OTPNotFoundException("No valid OTP found for mobile number: " + mobileNumber));
                        }
                    });
            
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to initialize SecureRandom with algorithm: {}", SECURE_RANDOM_ALGORITHM, e);
            throw new OTPGenerationFailedException("Failed to initialize OTP generator", e);
        }
    }

    /**
     * Generates a new OTP for the specified mobile number and sends it via SMS.
     * If an existing OTP is still valid, it will be invalidated before generating a new one.
     * 
     * @param mobileNumber The mobile number to send the OTP to (must be 10 digits)
     * @return OTPResponseDTO containing the reference ID and expiration time
     * @throws OTPException if OTP generation or SMS sending fails
     */
    @Override
    @Transactional
    public OTPResponseDTO generateOTP(String mobileNumber) throws OTPException {
        logger.info("Generating OTP for mobile number: {}", mobileNumber);
        
        // Check rate limiting
        checkRateLimit(mobileNumber);
        
        // Invalidate any existing OTPs for this mobile number
        invalidateOTP(mobileNumber);
        
        // Generate a new secure random OTP code
        String otpCode = generateSecureOTP(otpLength);
        
        // Create and persist the OTP entity
        OTP otp = new OTP(mobileNumber, otpCode);
        otp = otpRepository.save(otp);
        
        // Add to cache for faster verification
        otpCache.put(mobileNumber, otp);
        
        // Increment request count for rate limiting
        incrementRequestCount(mobileNumber);
        
        // Send OTP via SMS
        try {
            String message = formatOTPMessage(otpCode);
            boolean smsSent = smsService.sendSMS(mobileNumber, message);
            
            if (!smsSent) {
                throw new OTPDeliveryFailureException("Failed to send OTP via SMS to " + mobileNumber);
            }
            
            // Create response with reference ID (using OTP entity ID as reference)
            OTPResponseDTO response = new OTPResponseDTO(
                    otp.getId().toString(),
                    "SUCCESS",
                    "OTP sent successfully to your mobile number");
            
            logger.info("OTP generated successfully for mobile number: {}", mobileNumber);
            return response;
            
        } catch (Exception e) {
            logger.error("Error sending OTP via SMS to {}: {}", mobileNumber, e.getMessage(), e);
            throw new OTPDeliveryFailureException("Failed to send OTP via SMS", e);
        }
    }

    /**
     * Verifies the submitted OTP code against the stored OTP for the given mobile number.
     * This method checks if the OTP is valid, not expired, and matches the stored code.
     * It also tracks verification attempts and handles maximum attempt limits.
     * 
     * @param mobileNumber The mobile number associated with the OTP
     * @param otpCode The OTP code submitted by the user
     * @return true if verification is successful, false otherwise
     * @throws OTPException if verification fails due to invalid code, expiration, or max attempts
     */
    @Override
    @Transactional
    public boolean verifyOTP(String mobileNumber, String otpCode) throws OTPException {
        logger.info("Verifying OTP for mobile number: {}", mobileNumber);
        
        // Get the latest OTP for this mobile number
        OTP otp;
        try {
            // Try to get from cache first
            otp = otpCache.get(mobileNumber);
        } catch (Exception e) {
            // If not in cache, try to get from database
            Optional<OTP> latestOtp = otpRepository.findLatestValidOtpForMobile(
                    mobileNumber, LocalDateTime.now());
            
            if (!latestOtp.isPresent()) {
                logger.warn("No valid OTP found for mobile number: {}", mobileNumber);
                throw new OTPNotFoundException("No valid OTP found for mobile number: " + mobileNumber);
            }
            
            otp = latestOtp.get();
            // Update cache
            otpCache.put(mobileNumber, otp);
        }
        
        // Check if OTP has expired
        if (otp.isExpired()) {
            logger.warn("OTP has expired for mobile number: {}", mobileNumber);
            otp.markAsExpired();
            otpRepository.save(otp);
            otpCache.invalidate(mobileNumber);
            throw new OTPExpiredException.forMobileNumber(mobileNumber);
        }
        
        // Check if max attempts reached
        if (otp.isMaxAttemptsReached()) {
            logger.warn("Maximum verification attempts reached for mobile number: {}", mobileNumber);
            otp.markAsFailed();
            otpRepository.save(otp);
            otpCache.invalidate(mobileNumber);
            throw new OTPAttemptsExceededException(coolingPeriodSeconds);
        }
        
        // Increment attempt counter
        otp.incrementAttempts();
        
        // Verify OTP code
        if (!otp.getOtpCode().equals(otpCode)) {
            logger.warn("Invalid OTP provided for mobile number: {}", mobileNumber);
            otpRepository.save(otp);
            
            // Check if this attempt reached the max attempts
            if (otp.isMaxAttemptsReached()) {
                otp.markAsFailed();
                otpRepository.save(otp);
                otpCache.invalidate(mobileNumber);
                throw new OTPAttemptsExceededException(coolingPeriodSeconds);
            }
            
            // Calculate remaining attempts
            int remainingAttempts = maxVerificationAttempts - otp.getVerificationAttempts();
            throw new OTPInvalidException.forMobileNumber(mobileNumber, remainingAttempts);
        }
        
        // OTP is valid, mark as verified
        otp.markAsVerified();
        otpRepository.save(otp);
        otpCache.invalidate(mobileNumber);
        
        logger.info("OTP verified successfully for mobile number: {}", mobileNumber);
        return true;
    }

    /**
     * Checks if a mobile number has been successfully verified through the OTP process.
     * This is used to determine if a user can proceed with registration or other protected actions.
     * 
     * @param mobileNumber The mobile number to check verification status for
     * @return true if the mobile number has been verified, false otherwise
     */
    @Override
    public boolean checkMobileVerified(String mobileNumber) {
        logger.debug("Checking mobile verification status for: {}", mobileNumber);
        
        // Check if there's a verified OTP for this mobile number
        return !otpRepository.findByUserMobileNumberAndVerificationStatus(mobileNumber, "VERIFIED").isEmpty();
    }

    /**
     * Invalidates any existing OTPs for the specified mobile number.
     * This is typically used when a user requests a new OTP or when maximum verification
     * attempts have been reached.
     * 
     * @param mobileNumber The mobile number for which to invalidate OTPs
     * @return true if OTPs were successfully invalidated, false if no OTPs were found
     */
    @Override
    @Transactional
    public boolean invalidateOTP(String mobileNumber) {
        logger.debug("Invalidating existing OTPs for mobile number: {}", mobileNumber);
        
        // Remove from cache
        otpCache.invalidate(mobileNumber);
        
        // Get all pending OTPs for this mobile number
        LocalDateTime now = LocalDateTime.now();
        Optional<OTP> latestOtp = otpRepository.findLatestValidOtpForMobile(mobileNumber, now);
        
        if (latestOtp.isPresent()) {
            OTP otp = latestOtp.get();
            otp.markAsExpired();
            otpRepository.save(otp);
            return true;
        }
        
        return false;
    }

    /**
     * Returns the configured OTP expiration time in minutes.
     * This is used for informational purposes to let users know how long they have to verify.
     * 
     * @return The OTP expiration time in minutes
     */
    @Override
    public int getOTPExpirationTime() {
        return otpExpirationSeconds / 60; // Convert seconds to minutes
    }

    /**
     * Returns the maximum number of verification attempts allowed for an OTP.
     * After this limit is reached, the OTP is invalidated and a new one must be generated.
     * 
     * @return The maximum number of verification attempts allowed
     */
    @Override
    public int getMaxVerificationAttempts() {
        return maxVerificationAttempts;
    }

    /**
     * Returns the remaining verification attempts for the given mobile number.
     * This is used to inform users how many attempts they have left before the OTP is invalidated.
     * 
     * @param mobileNumber The mobile number to check remaining attempts for
     * @return The number of remaining verification attempts, or 0 if no valid OTP exists
     */
    @Override
    public int getRemainingVerificationAttempts(String mobileNumber) {
        try {
            // Try to get from cache first
            OTP otp = otpCache.get(mobileNumber);
            return Math.max(0, maxVerificationAttempts - otp.getVerificationAttempts());
        } catch (Exception e) {
            // If not in cache, try to get from database
            Optional<OTP> latestOtp = otpRepository.findLatestValidOtpForMobile(
                    mobileNumber, LocalDateTime.now());
            
            if (latestOtp.isPresent()) {
                return Math.max(0, maxVerificationAttempts - latestOtp.get().getVerificationAttempts());
            }
            
            return 0; // No valid OTP exists
        }
    }
    
    /**
     * Generates a secure random OTP code of the specified length.
     * Uses SecureRandom to ensure cryptographically strong randomness.
     * 
     * @param length The length of the OTP code to generate
     * @return A secure random OTP code
     */
    private String generateSecureOTP(int length) {
        StringBuilder otpBuilder = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(OTP_CHARS.length());
            otpBuilder.append(OTP_CHARS.charAt(randomIndex));
        }
        
        return otpBuilder.toString();
    }
    
    /**
     * Formats the OTP message to be sent via SMS.
     * 
     * @param otpCode The OTP code to include in the message
     * @return The formatted message text
     */
    private String formatOTPMessage(String otpCode) {
        int expirationMinutes = getOTPExpirationTime();
        return String.format("Your verification code is %s. It will expire in %d minutes. Do not share this code with anyone.", 
                otpCode, expirationMinutes);
    }
    
    /**
     * Checks if the mobile number has exceeded the rate limit for OTP requests.
     * Throws OTPRateLimitedException if the limit is exceeded.
     * 
     * @param mobileNumber The mobile number to check
     * @throws OTPRateLimitedException if rate limit is exceeded
     */
    private void checkRateLimit(String mobileNumber) throws OTPRateLimitedException {
        try {
            int requestCount = otpRequestCountCache.get(mobileNumber);
            
            if (requestCount >= maxRequestsPerWindow) {
                logger.warn("Rate limit exceeded for mobile number: {}", mobileNumber);
                throw OTPRateLimitedException.forMobileNumber(mobileNumber, rateLimitWindowHours * 3600);
            }
            
            // Also check database for additional verification
            LocalDateTime windowStart = LocalDateTime.now().minusHours(rateLimitWindowHours);
            long dbRequestCount = otpRepository.countRecentOtpsForMobile(mobileNumber, windowStart);
            
            if (dbRequestCount >= maxRequestsPerWindow) {
                logger.warn("Rate limit exceeded (database check) for mobile number: {}", mobileNumber);
                throw OTPRateLimitedException.forMobileNumber(mobileNumber, rateLimitWindowHours * 3600);
            }
            
        } catch (OTPRateLimitedException e) {
            throw e;
        } catch (Exception e) {
            // If cache access fails, fall back to database check only
            LocalDateTime windowStart = LocalDateTime.now().minusHours(rateLimitWindowHours);
            long dbRequestCount = otpRepository.countRecentOtpsForMobile(mobileNumber, windowStart);
            
            if (dbRequestCount >= maxRequestsPerWindow) {
                logger.warn("Rate limit exceeded (database fallback) for mobile number: {}", mobileNumber);
                throw OTPRateLimitedException.forMobileNumber(mobileNumber, rateLimitWindowHours * 3600);
            }
        }
    }
    
    /**
     * Increments the request count for the given mobile number in the rate limiting cache.
     * 
     * @param mobileNumber The mobile number to increment the count for
     */
    private void incrementRequestCount(String mobileNumber) {
        try {
            int currentCount = otpRequestCountCache.get(mobileNumber);
            otpRequestCountCache.put(mobileNumber, currentCount + 1);
        } catch (Exception e) {
            logger.warn("Failed to increment request count for {}: {}", mobileNumber, e.getMessage());
            // Continue execution even if cache update fails
            // The database check in checkRateLimit will serve as a fallback
        }
    }
}