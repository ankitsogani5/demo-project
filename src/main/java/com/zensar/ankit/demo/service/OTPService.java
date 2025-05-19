package com.zensar.ankit.demo.service;

import com.zensar.ankit.demo.dto.OTPResponseDTO;
import com.zensar.ankit.demo.exception.OTPException;

/**
 * Service interface for OTP (One-Time Password) generation, verification, and management.
 * This service is responsible for handling all aspects of the mobile verification process
 * including generating OTPs, sending them via SMS, verifying submitted codes, and tracking
 * verification status.
 */
public interface OTPService {
    
    /**
     * Generates a new OTP for the specified mobile number and sends it via SMS.
     * If an existing OTP is still valid, it will be invalidated before generating a new one.
     * 
     * @param mobileNumber The mobile number to send the OTP to (must be 10 digits)
     * @return OTPResponseDTO containing the reference ID and expiration time
     * @throws OTPException if OTP generation or SMS sending fails
     */
    OTPResponseDTO generateOTP(String mobileNumber) throws OTPException;
    
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
    boolean verifyOTP(String mobileNumber, String otpCode) throws OTPException;
    
    /**
     * Checks if a mobile number has been successfully verified through the OTP process.
     * This is used to determine if a user can proceed with registration or other protected actions.
     * 
     * @param mobileNumber The mobile number to check verification status for
     * @return true if the mobile number has been verified, false otherwise
     */
    boolean checkMobileVerified(String mobileNumber);
    
    /**
     * Invalidates any existing OTPs for the specified mobile number.
     * This is typically used when a user requests a new OTP or when maximum verification
     * attempts have been reached.
     * 
     * @param mobileNumber The mobile number for which to invalidate OTPs
     * @return true if OTPs were successfully invalidated, false if no OTPs were found
     */
    boolean invalidateOTP(String mobileNumber);
    
    /**
     * Returns the configured OTP expiration time in minutes.
     * This is used for informational purposes to let users know how long they have to verify.
     * 
     * @return The OTP expiration time in minutes
     */
    int getOTPExpirationTime();
    
    /**
     * Returns the maximum number of verification attempts allowed for an OTP.
     * After this limit is reached, the OTP is invalidated and a new one must be generated.
     * 
     * @return The maximum number of verification attempts allowed
     */
    int getMaxVerificationAttempts();
    
    /**
     * Returns the remaining verification attempts for the given mobile number.
     * This is used to inform users how many attempts they have left before the OTP is invalidated.
     * 
     * @param mobileNumber The mobile number to check remaining attempts for
     * @return The number of remaining verification attempts, or 0 if no valid OTP exists
     */
    int getRemainingVerificationAttempts(String mobileNumber);
}