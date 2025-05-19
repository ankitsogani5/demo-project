package com.zensar.ankit.demo.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zensar.ankit.demo.entity.User;
import com.zensar.ankit.demo.exception.OTPException;
import com.zensar.ankit.demo.service.OTPService;
import com.zensar.ankit.demo.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller for user registration operations.
 * Handles user creation with OTP verification for mobile numbers.
 */
@RestController
@RequestMapping(value = "user")
@Api(value="user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OTPService otpService;
	
	/**
	 * Registers a new user after validating that their mobile number has been verified.
	 * The mobile number must be verified through the OTP verification process before
	 * the user can be registered.
	 * 
	 * @param user The user entity to be registered
	 * @return The registered user with generated ID
	 * @throws OTPException if the mobile number has not been verified
	 */
	@ApiOperation(value = "Register user with verified mobile number", response = User.class, 
		notes = "Mobile number must be verified through OTP verification process before registration")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User successfully registered"),
            @ApiResponse(code = 400, message = "Bad Request, request provided is not valid"),
            @ApiResponse(code = 401, message = "Unauthorized, mobile number not verified"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 429, message = "Too many requests, rate limit exceeded")
    })
	@PostMapping(value = "/")
	public ResponseEntity<User> create(@Valid @RequestBody User user) {
		// Check if the mobile number has been verified
		if (!otpService.checkMobileVerified(user.getMobileNumber())) {
			// If not verified, return 401 Unauthorized
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		// Mobile number is verified, proceed with user registration
		User savedUser = userService.saveWithVerification(user);
		return new ResponseEntity<>(savedUser, HttpStatus.OK);
	}
}