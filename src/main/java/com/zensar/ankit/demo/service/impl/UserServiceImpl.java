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
 * Implementation of the UserService interface.
 * This service handles user registration with mobile verification through OTP.
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
				"Mobile number " + user.getMobileNumber() + " has not been verified through OTP",
				"MOBILE_NOT_VERIFIED",
				HttpStatus.UNAUTHORIZED
			);
		}
		
		// Set the mobile verification status to true
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
		// Check if the mobile number has been verified using the OTP service
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
		// Since we don't have a direct findByMobileNumber method in the repository,
		// we need to find all users and filter manually
		// This is not efficient for production but works for this demo
		for (User user : repository.findAll()) {
			if (user.getMobileNumber() != null && user.getMobileNumber().equals(mobileNumber)) {
				user.setMobileVerificationStatus(verified);
				repository.save(user);
				return true;
			}
		}
		
		// If user doesn't exist yet, we'll consider this a pre-verification
		// The status will be set when the user is created
		return false;
	}
}
