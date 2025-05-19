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

@RestController
@RequestMapping(value = "user")
@Api(value="user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OTPService otpService;
	
	/**
	 * Registers a new user after verifying that their mobile number has been validated through OTP verification.
	 * 
	 * @param user The user object containing registration details including mobile number
	 * @return The registered user object with generated ID
	 * @throws OTPException if the mobile number has not been verified
	 */
	@ApiOperation(value = "Register user after mobile verification", response = User.class, notes = "Registers a new user after verifying that their mobile number has been validated through OTP verification. The mobile number must be verified using the OTP verification process before calling this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User successfully registered"),
            @ApiResponse(code = 400, message = "Bad Request, request provided is not valid"),
            @ApiResponse(code = 403, message = "Mobile number not verified, please complete OTP verification first"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
	@PostMapping(value = "/")
	public ResponseEntity<User> create(@Valid @RequestBody User user) {
		// Check if the mobile number has been verified
		if (!otpService.checkMobileVerified(user.getMobileNumber())) {
			// Mobile number not verified, return error response
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		
		// Mobile number verified, proceed with user registration
		User savedUser = userService.save(user);
		return new ResponseEntity<>(savedUser, HttpStatus.OK);
	}

}