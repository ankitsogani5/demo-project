package com.zensar.ankit.demo.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zensar.ankit.demo.dto.OTPRequestDTO;
import com.zensar.ankit.demo.dto.OTPResponseDTO;
import com.zensar.ankit.demo.dto.OTPVerificationDTO;
import com.zensar.ankit.demo.exception.OTPAlreadyVerifiedException;
import com.zensar.ankit.demo.exception.OTPAttemptsExceededException;
import com.zensar.ankit.demo.exception.OTPDeliveryFailureException;
import com.zensar.ankit.demo.exception.OTPExpiredException;
import com.zensar.ankit.demo.exception.OTPGenerationFailedException;
import com.zensar.ankit.demo.exception.OTPInvalidException;
import com.zensar.ankit.demo.exception.OTPNotFoundException;
import com.zensar.ankit.demo.exception.OTPRateLimitedException;
import com.zensar.ankit.demo.service.OTPService;
import com.zensar.ankit.demo.service.SMSService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller for handling OTP generation and verification operations.
 * This controller provides endpoints for generating one-time passwords for mobile verification
 * and validating submitted OTP codes.
 * 
 * Rate limiting is enforced for OTP generation with a maximum of 3 requests per mobile number
 * per 10 minutes to prevent abuse. OTP verification is limited to 3 attempts per OTP code.
 */
@RestController
@RequestMapping(value = "otp")
@Api(value = "otp", description = "Operations for OTP generation and verification")
public class OTPController {

    @Autowired
    private OTPService otpService;
    
    @Autowired
    private SMSService smsService;
    
    /**
     * Generates a new OTP for the provided mobile number and sends it via SMS.
     * Rate limiting is enforced with a maximum of 3 requests per mobile number per 10 minutes.
     * 
     * @param otpRequest The request containing the mobile number for OTP generation
     * @return ResponseEntity containing the OTP generation response with request ID
     * @throws OTPRateLimitedException if rate limit is exceeded for the mobile number
     * @throws OTPGenerationFailedException if OTP generation fails
     * @throws OTPDeliveryFailureException if SMS delivery fails
     */
    @ApiOperation(value = "Generate OTP for mobile verification", response = OTPResponseDTO.class, 
            notes = "Generates a 6-digit OTP and sends it to the provided mobile number via SMS. "  +
                   "Rate limited to 3 requests per mobile number per 10 minutes.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "OTP generated and sent successfully"),
            @ApiResponse(code = 400, message = "Invalid mobile number format"),
            @ApiResponse(code = 429, message = "Rate limit exceeded for the mobile number (3 requests per 10 minutes)"),
            @ApiResponse(code = 500, message = "Error in generating or sending OTP"),
            @ApiResponse(code = 503, message = "SMS delivery service unavailable")
    })
    @PostMapping(value = "/generate")
    public ResponseEntity<OTPResponseDTO> generateOTP(@Valid @RequestBody OTPRequestDTO otpRequest) {
        // Generate OTP and send via SMS
        OTPResponseDTO response = otpService.generateOTP(otpRequest.getMobileNumber());
        
        // Return response with CREATED status
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Verifies the submitted OTP code against the generated OTP for the mobile number.
     * A maximum of 3 verification attempts are allowed per OTP code.
     * 
     * @param verificationRequest The request containing the mobile number and OTP code
     * @return ResponseEntity containing the verification result
     * @throws OTPNotFoundException if no OTP is found for the mobile number
     * @throws OTPExpiredException if the OTP has expired
     * @throws OTPInvalidException if the OTP code is invalid
     * @throws OTPAttemptsExceededException if maximum verification attempts are exceeded
     * @throws OTPAlreadyVerifiedException if the OTP has already been verified
     */
    @ApiOperation(value = "Verify OTP code for mobile verification", response = OTPResponseDTO.class,
            notes = "Validates the submitted OTP code against the generated OTP for the mobile number. " +
                   "Limited to 3 verification attempts per OTP code.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OTP verified successfully"),
            @ApiResponse(code = 400, message = "Invalid request format or invalid OTP code"),
            @ApiResponse(code = 401, message = "Invalid OTP code"),
            @ApiResponse(code = 404, message = "No OTP found for the mobile number"),
            @ApiResponse(code = 410, message = "OTP expired (valid for 10 minutes)"),
            @ApiResponse(code = 429, message = "Maximum verification attempts exceeded (3 attempts per OTP)")
    })
    @PostMapping(value = "/verify")
    public ResponseEntity<OTPResponseDTO> verifyOTP(@Valid @RequestBody OTPVerificationDTO verificationRequest) {
        // Verify the submitted OTP
        OTPResponseDTO response = otpService.verifyOTP(
                verificationRequest.getMobileNumber(), 
                verificationRequest.getOtpCode());
        
        // Return response with OK status
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}