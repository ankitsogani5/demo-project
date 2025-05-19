package com.zensar.ankit.demo.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.zensar.ankit.demo.entity.OTP;
import com.zensar.ankit.demo.repository.OTPRepository;
import com.zensar.ankit.demo.service.OTPService;
import com.zensar.ankit.demo.service.SMSService;

/**
 * Implementation of the OTPService interface that provides OTP generation,
 * verification, and management functionality.
 * 
 * This service is responsible for creating secure random OTP codes, sending them via SMS,
 * verifying submitted codes, and managing expiration. It uses Google Guava for caching
 * OTP codes to improve performance and implements rate limiting to prevent abuse.
 */
@Service
public class OTPServiceImpl implements OTPService {
    
    private static final Logger logger = LoggerFactory.getLogger(OTPServiceImpl.class);
    
    // Constants for OTP generation and validation
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 10;
    private static final int MAX_VERIFICATION_ATTEMPTS = 3;
    private static final int RATE_LIMIT_WINDOW_MINUTES = 10;
    private static final int MAX_REQUESTS_PER_WINDOW = 3;
    
    // Cache for storing OTP entities with 10-minute expiration
    private final LoadingCache<String, OTP> otpCache;
    
    // Cache for rate limiting OTP generation requests
    private final LoadingCache<String, Integer> rateLimitCache;
    
    @Autowired
    private OTPRepository otpRepository;
    
    @Autowired
    private SMSService smsService;
    
    // SecureRandom for generating cryptographically strong random OTP codes
    private final SecureRandom secureRandom;
    
    /**
     * Constructor that initializes the caches and secure random generator.
     */
    public OTPServiceImpl() {
        // Initialize SecureRandom
        this.secureRandom = new SecureRandom();
        
        // Initialize OTP cache with 10-minute expiration
        this.otpCache = CacheBuilder.newBuilder()
                .maximumSize(10000) // Maximum cache size
                .expireAfterWrite(OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES)
                .build(new CacheLoader<String, OTP>() {
                    @Override
                    public OTP load(String mobileNumber) throws Exception {
                        // Load OTP from database if not in cache
                        return otpRepository.findLatestValidOtpForMobile(mobileNumber)
                                .orElseThrow(() -> new Exception("No valid OTP found for mobile number: " + mobileNumber));
                    }
                });
        
        // Initialize rate limit cache with 10-minute window
        this.rateLimitCache = CacheBuilder.newBuilder()
                .maximumSize(10000) // Maximum cache size
                .expireAfterWrite(RATE_LIMIT_WINDOW_MINUTES, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0; // Initial count is 0
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public String generateOTP(String mobileNumber) {
        logger.info("Generating OTP for mobile number: {}", mobileNumber);
        
        // Validate mobile number format
        validateMobileNumber(mobileNumber);
        
        // Check rate limiting
        checkRateLimit(mobileNumber);
        
        // Generate a secure random 6-digit OTP
        String otpCode = generateRandomOTP();
        
        // Create and persist OTP entity
        OTP otp = new OTP(mobileNumber, otpCode);
        otp = otpRepository.save(otp);
        
        // Add to cache for faster retrieval during verification
        otpCache.put(mobileNumber, otp);
        
        // Send OTP via SMS
        boolean smsSent = sendOTPViaSMS(mobileNumber, otpCode);
        if (!smsSent) {
            logger.error("Failed to send OTP via SMS to mobile number: {}", mobileNumber);
            throw new RuntimeException("Failed to send OTP via SMS");
        }
        
        // Return a reference ID (using the OTP entity ID)
        return String.valueOf(otp.getId());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean verifyOTP(String mobileNumber, String otpCode) {
        logger.info("Verifying OTP for mobile number: {}", mobileNumber);
        
        // Validate inputs
        validateMobileNumber(mobileNumber);
        validateOTPCode(otpCode);
        
        try {
            // Try to get OTP from cache first
            OTP otp = otpCache.getUnchecked(mobileNumber);
            
            // If not in cache or exception occurred, try to get from database
            if (otp == null) {
                otp = otpRepository.findLatestValidOtpForMobile(mobileNumber)
                        .orElseThrow(() -> new RuntimeException("No valid OTP found for mobile number: " + mobileNumber));
            }
            
            // Check if OTP is expired
            if (otp.isExpired()) {
                logger.info("OTP has expired for mobile number: {}", mobileNumber);
                otp.markAsExpired();
                otpRepository.save(otp);
                return false;
            }
            
            // Check if max attempts reached
            if (otp.isMaxAttemptsReached()) {
                logger.info("Maximum verification attempts reached for mobile number: {}", mobileNumber);
                otp.markAsFailed();
                otpRepository.save(otp);
                return false;
            }
            
            // Increment attempt counter
            otp.incrementAttempts();
            
            // Verify OTP code
            boolean isValid = otp.getOtpCode().equals(otpCode);
            
            if (isValid) {
                // Mark as verified if OTP is correct
                otp.markAsVerified();
                logger.info("OTP verified successfully for mobile number: {}", mobileNumber);
            } else if (otp.isMaxAttemptsReached()) {
                // Mark as failed if max attempts reached after this attempt
                otp.markAsFailed();
                logger.info("OTP verification failed (max attempts) for mobile number: {}", mobileNumber);
            } else {
                logger.info("OTP verification failed for mobile number: {}, attempts: {}", 
                        mobileNumber, otp.getVerificationAttempts());
            }
            
            // Save updated OTP entity
            otpRepository.save(otp);
            
            // Update cache with the latest state
            if (isValid) {
                otpCache.invalidate(mobileNumber);
            } else {
                otpCache.put(mobileNumber, otp);
            }
            
            return isValid;
        } catch (Exception e) {
            logger.error("Error verifying OTP for mobile number: {}", mobileNumber, e);
            return false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkMobileVerified(String mobileNumber) {
        logger.info("Checking mobile verification status for: {}", mobileNumber);
        
        // Validate mobile number format
        validateMobileNumber(mobileNumber);
        
        // Check if any OTP for this mobile number has been verified
        return otpRepository.findByUserMobileNumber(mobileNumber).stream()
                .anyMatch(otp -> "VERIFIED".equals(otp.getVerificationStatus()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getRemainingAttempts(String mobileNumber) {
        try {
            OTP otp = otpCache.getUnchecked(mobileNumber);
            if (otp != null && !otp.isExpired() && "PENDING".equals(otp.getVerificationStatus())) {
                return MAX_VERIFICATION_ATTEMPTS - otp.getVerificationAttempts();
            }
        } catch (Exception e) {
            // If not in cache, try database
            return otpRepository.findLatestValidOtpForMobile(mobileNumber)
                    .map(otp -> MAX_VERIFICATION_ATTEMPTS - otp.getVerificationAttempts())
                    .orElse(0);
        }
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOTPExpired(String mobileNumber) {
        try {
            OTP otp = otpCache.getUnchecked(mobileNumber);
            return otp == null || otp.isExpired();
        } catch (Exception e) {
            // If not in cache, try database
            return otpRepository.findLatestValidOtpForMobile(mobileNumber)
                    .map(OTP::isExpired)
                    .orElse(true); // If no OTP found, consider it expired
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getOTPExpirationTime(String mobileNumber) {
        try {
            OTP otp = otpCache.getUnchecked(mobileNumber);
            return otp != null ? otp.getExpirationTimestamp() : null;
        } catch (Exception e) {
            // If not in cache, try database
            return otpRepository.findLatestValidOtpForMobile(mobileNumber)
                    .map(OTP::getExpirationTimestamp)
                    .orElse(null);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean invalidateOTP(String mobileNumber) {
        logger.info("Invalidating OTP for mobile number: {}", mobileNumber);
        
        try {
            // Remove from cache
            otpCache.invalidate(mobileNumber);
            
            // Update in database
            return otpRepository.findLatestValidOtpForMobile(mobileNumber)
                    .map(otp -> {
                        otp.markAsExpired();
                        otpRepository.save(otp);
                        return true;
                    })
                    .orElse(false);
        } catch (Exception e) {
            logger.error("Error invalidating OTP for mobile number: {}", mobileNumber, e);
            return false;
        }
    }
    
    /**
     * Generates a secure random 6-digit OTP code.
     * 
     * @return A 6-digit OTP code as a string
     */
    private String generateRandomOTP() {
        // Generate a random number between 0 and 999999
        int randomNumber = secureRandom.nextInt(1000000);
        
        // Format as a 6-digit string with leading zeros if needed
        return String.format("%06d", randomNumber);
    }
    
    /**
     * Sends the OTP code to the specified mobile number via SMS.
     * 
     * @param mobileNumber The mobile number to send the OTP to
     * @param otpCode The OTP code to send
     * @return true if the SMS was sent successfully, false otherwise
     */
    private boolean sendOTPViaSMS(String mobileNumber, String otpCode) {
        String message = String.format("Your verification code is %s. Valid for %d minutes.", 
                otpCode, OTP_EXPIRATION_MINUTES);
        
        return smsService.sendSMS(mobileNumber, message);
    }
    
    /**
     * Validates the mobile number format.
     * 
     * @param mobileNumber The mobile number to validate
     * @throws IllegalArgumentException if the mobile number format is invalid
     */
    private void validateMobileNumber(String mobileNumber) {
        if (mobileNumber == null || !mobileNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Invalid mobile number format. Must be a 10-digit number.");
        }
    }
    
    /**
     * Validates the OTP code format.
     * 
     * @param otpCode The OTP code to validate
     * @throws IllegalArgumentException if the OTP code format is invalid
     */
    private void validateOTPCode(String otpCode) {
        if (otpCode == null || !otpCode.matches("\\d{" + OTP_LENGTH + "}")) {
            throw new IllegalArgumentException("Invalid OTP format. Must be a " + OTP_LENGTH + "-digit number.");
        }
    }
    
    /**
     * Checks if the mobile number has exceeded the rate limit for OTP generation.
     * 
     * @param mobileNumber The mobile number to check
     * @throws RuntimeException if the rate limit has been exceeded
     */
    private void checkRateLimit(String mobileNumber) {
        try {
            // Get current count from cache
            int currentCount = rateLimitCache.get(mobileNumber);
            
            // Check if limit exceeded
            if (currentCount >= MAX_REQUESTS_PER_WINDOW) {
                logger.warn("Rate limit exceeded for mobile number: {}", mobileNumber);
                throw new RuntimeException("Rate limit exceeded. Please try again later.");
            }
            
            // Increment count
            rateLimitCache.put(mobileNumber, currentCount + 1);
            
            // Double-check with database for additional security
            LocalDateTime windowStart = LocalDateTime.now().minusMinutes(RATE_LIMIT_WINDOW_MINUTES);
            int dbCount = otpRepository.countRecentOtpsForMobile(mobileNumber, windowStart);
            
            if (dbCount >= MAX_REQUESTS_PER_WINDOW) {
                logger.warn("Database rate limit check exceeded for mobile number: {}", mobileNumber);
                throw new RuntimeException("Rate limit exceeded. Please try again later.");
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("Rate limit exceeded")) {
                throw (RuntimeException) e;
            }
            // For cache loading exceptions, default to database check
            LocalDateTime windowStart = LocalDateTime.now().minusMinutes(RATE_LIMIT_WINDOW_MINUTES);
            int dbCount = otpRepository.countRecentOtpsForMobile(mobileNumber, windowStart);
            
            if (dbCount >= MAX_REQUESTS_PER_WINDOW) {
                logger.warn("Database rate limit check exceeded for mobile number: {}", mobileNumber);
                throw new RuntimeException("Rate limit exceeded. Please try again later.");
            }
            
            // Initialize cache with current count from database
            rateLimitCache.put(mobileNumber, dbCount + 1);
        }
    }
}