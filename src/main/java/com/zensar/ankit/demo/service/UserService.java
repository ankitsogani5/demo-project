package com.zensar.ankit.demo.service;

import com.zensar.ankit.demo.entity.User;

public interface UserService {
	/**
	 * Saves a user to the database without verification checks.
	 * This method should only be used for internal purposes or when verification
	 * has already been confirmed elsewhere.
	 * 
	 * @param user The user entity to save
	 * @return The saved user with generated ID
	 */
	public User save(User user);
	
	/**
	 * Saves a user to the database after verifying that their mobile number
	 * has been successfully verified through the OTP verification process.
	 * 
	 * @param user The user entity to save
	 * @return The saved user with generated ID
	 * @throws com.zensar.ankit.demo.exception.OTPException if the mobile number has not been verified
	 */
	public User saveWithVerification(User user);
	
	/**
	 * Checks if a mobile number has been verified through the OTP verification process.
	 * 
	 * @param mobileNumber The mobile number to check
	 * @return true if the mobile number has been verified, false otherwise
	 */
	public boolean isMobileNumberVerified(String mobileNumber);
	
	/**
	 * Updates the mobile verification status for a user.
	 * This method is typically called by the OTP service after successful verification.
	 * 
	 * @param mobileNumber The mobile number that has been verified
	 * @param verified The verification status to set
	 * @return true if the update was successful, false otherwise
	 */
	public boolean updateMobileVerificationStatus(String mobileNumber, boolean verified);
}