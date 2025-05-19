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
import com.zensar.ankit.demo.service.OTPService;
import com.zensar.ankit.demo.service.SMSService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller for OTP generation and verification endpoints.
 * Provides REST API endpoints for generating and validating one-time passwords
 * for mobile number verification during user registration.
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
     * Implements rate limiting of 3 requests per mobile number per 10 minutes.
     * 
     * @param otpRequest Contains the mobile number and associated user information
     * @return OTPResponseDTO with request ID and status information
     */
    @ApiOperation(value = "Generate OTP for mobile verification", response = OTPResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OTP generated and sent successfully"),
            @ApiResponse(code = 400, message = "Invalid mobile number format"),
            @ApiResponse(code = 429, message = "Rate limit exceeded - too many requests"),
            @ApiResponse(code = 500, message = "Failed to send OTP via SMS")
    })
    @PostMapping(value = "/generate")
    public ResponseEntity<OTPResponseDTO> generateOTP(@Valid @RequestBody OTPRequestDTO otpRequest) {
        // Generate OTP and send via SMS
        OTPResponseDTO response = otpService.generateOTP(otpRequest.getMobileNumber());
        
        // Return response with request ID
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Verifies the submitted OTP against the stored value.
     * Validates that the OTP has not expired and that maximum attempts have not been exceeded.
     * 
     * @param verificationRequest Contains the request ID and OTP code
     * @return Response indicating verification success or failure
     */
    @ApiOperation(value = "Verify OTP code", notes = "Validates the submitted OTP code against the stored value")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OTP verified successfully"),
            @ApiResponse(code = 400, message = "Invalid OTP or request ID"),
            @ApiResponse(code = 404, message = "OTP request not found"),
            @ApiResponse(code = 410, message = "OTP expired"),
            @ApiResponse(code = 429, message = "Maximum verification attempts exceeded")
    })
    @PostMapping(value = "/verify")
    public ResponseEntity<OTPResponseDTO> verifyOTP(@Valid @RequestBody OTPVerificationDTO verificationRequest) {
        // Verify the submitted OTP
        OTPResponseDTO response = otpService.verifyOTP(
                verificationRequest.getRequestId(), 
                verificationRequest.getOtpCode());
        
        // Return verification result
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}