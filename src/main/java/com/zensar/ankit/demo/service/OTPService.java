package com.zensar.ankit.demo.service;

import java.time.LocalDateTime;

/**
 * Service interface for OTP (One-Time Password) generation, verification, and management.
 * This service is responsible for creating time-limited OTP codes, verifying submitted codes,
 * and checking verification status for mobile numbers during the user registration process.
 */
public interface OTPService {
    
    /**
     * Generates a new 6-digit OTP for the specified mobile number and sends it via SMS.
     * The generated OTP is valid for 10 minutes from creation time.
     * 
     * @param mobileNumber The mobile number to send the OTP to (must be a valid 10-digit number)
     * @return A reference ID that can be used to track this OTP verification request
     * @throws IllegalArgumentException if the mobile number format is invalid
     * @throws RuntimeException if OTP generation or SMS delivery fails
     */
    String generateOTP(String mobileNumber);
    
    /**
     * Verifies the submitted OTP code against the stored OTP for the given mobile number.
     * Verification fails if the OTP is incorrect, expired, or maximum attempts are exceeded.
     * 
     * @param mobileNumber The mobile number associated with the OTP
     * @param otpCode The 6-digit OTP code submitted by the user
     * @return true if verification is successful, false otherwise
     * @throws IllegalArgumentException if the mobile number or OTP format is invalid
     * @throws RuntimeException if verification process encounters an error
     */
    boolean verifyOTP(String mobileNumber, String otpCode);
    
    /**
     * Checks if a mobile number has been successfully verified through the OTP process.
     * This method is used during user registration to ensure mobile verification is complete.
     * 
     * @param mobileNumber The mobile number to check verification status for
     * @return true if the mobile number has been verified, false otherwise
     */
    boolean checkMobileVerified(String mobileNumber);
    
    /**
     * Retrieves the remaining verification attempts for the latest OTP sent to a mobile number.
     * The system allows a maximum of 3 verification attempts per OTP.
     * 
     * @param mobileNumber The mobile number to check remaining attempts for
     * @return The number of remaining verification attempts (0-3), or 0 if no valid OTP exists
     */
    int getRemainingAttempts(String mobileNumber);
    
    /**
     * Checks if the OTP for a given mobile number has expired.
     * OTPs are valid for 10 minutes from the time of generation.
     * 
     * @param mobileNumber The mobile number to check OTP expiration for
     * @return true if the OTP has expired or doesn't exist, false if it's still valid
     */
    boolean isOTPExpired(String mobileNumber);
    
    /**
     * Retrieves the expiration time for the latest OTP sent to a mobile number.
     * 
     * @param mobileNumber The mobile number to get OTP expiration time for
     * @return The expiration timestamp, or null if no valid OTP exists
     */
    LocalDateTime getOTPExpirationTime(String mobileNumber);
    
    /**
     * Invalidates any existing OTP for the given mobile number.
     * This is useful when a user requests a new OTP before the old one expires.
     * 
     * @param mobileNumber The mobile number to invalidate OTPs for
     * @return true if an OTP was invalidated, false if no valid OTP existed
     */
    boolean invalidateOTP(String mobileNumber);
}