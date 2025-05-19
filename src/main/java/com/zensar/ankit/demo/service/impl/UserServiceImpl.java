package com.zensar.ankit.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zensar.ankit.demo.entity.User;
import com.zensar.ankit.demo.exception.OTPException;
import com.zensar.ankit.demo.repository.UserRepository;
import com.zensar.ankit.demo.service.OTPService;
import com.zensar.ankit.demo.service.UserService;

/**
 * Implementation of the UserService interface that handles user registration
 * with OTP verification for mobile numbers.
 */
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private OTPService otpService;

	/**
	 * Saves a user to the database without verification checks.
	 * This method should only be used for internal purposes or when verification
	 * has already been confirmed elsewhere.
	 * 
	 * @param user The user entity to save
	 * @return The saved user with generated ID
	 */
	@Override
	@Transactional
	public User save(User user) {
		return repository.save(user);
	}
	
	/**
	 * Saves a user to the database after verifying that their mobile number
	 * has been successfully verified through the OTP verification process.
	 * 
	 * @param user The user entity to save
	 * @return The saved user with generated ID
	 * @throws OTPException if the mobile number has not been verified
	 */
	@Override
	@Transactional
	public User saveWithVerification(User user) {
		// Check if the mobile number has been verified
		if (!isMobileNumberVerified(user.getMobileNumber())) {
			throw new OTPException(
				"OTP_NOT_VERIFIED", 
				"Mobile number has not been verified. Please complete OTP verification before registration.", 
				HttpStatus.BAD_REQUEST
			);
		}
		
		// Set the verification status to true
		user.setMobileVerificationStatus(true);
		
		// Save the user
		return repository.save(user);
	}
	
	/**
	 * Checks if a mobile number has been verified through the OTP verification process.
	 * 
	 * @param mobileNumber The mobile number to check
	 * @return true if the mobile number has been verified, false otherwise
	 */
	@Override
	public boolean isMobileNumberVerified(String mobileNumber) {
		if (mobileNumber == null || mobileNumber.isEmpty()) {
			return false;
		}
		
		// Delegate to OTP service to check verification status
		return otpService.checkMobileVerified(mobileNumber);
	}
	
	/**
	 * Updates the mobile verification status for a user.
	 * This method is typically called by the OTP service after successful verification.
	 * 
	 * @param mobileNumber The mobile number that has been verified
	 * @param verified The verification status to set
	 * @return true if the update was successful, false otherwise
	 */
	@Override
	@Transactional
	public boolean updateMobileVerificationStatus(String mobileNumber, boolean verified) {
		try {
			// Find users with this mobile number
			User user = repository.findByMobileNumber(mobileNumber);
			
			// If user exists, update verification status
			if (user != null) {
				user.setMobileVerificationStatus(verified);
				repository.save(user);
				return true;
			}
			
			// No user found with this mobile number
			return false;
		} catch (Exception e) {
			// Log the exception
			System.err.println("Error updating mobile verification status: " + e.getMessage());
			return false;
		}
	}
}