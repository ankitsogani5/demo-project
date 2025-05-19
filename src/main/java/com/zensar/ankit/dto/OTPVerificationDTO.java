package com.zensar.ankit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for OTP verification requests.
 * This class encapsulates the request ID and OTP code needed to verify a previously generated OTP.
 */
public class OTPVerificationDTO {

    @NotBlank(message = "Request ID cannot be empty")
    @Schema(description = "Unique identifier for the OTP verification session", 
            example = "a1b2c3d4-e5f6-7890", 
            required = true)
    private String requestId;

    @NotBlank(message = "OTP code cannot be empty")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be a 6-digit number")
    @Schema(description = "6-digit OTP code received via SMS", 
            example = "123456", 
            required = true)
    private String otpCode;

    /**
     * Default constructor
     */
    public OTPVerificationDTO() {
    }

    /**
     * Parameterized constructor
     * 
     * @param requestId Unique identifier for the OTP verification session
     * @param otpCode 6-digit OTP code received via SMS
     */
    public OTPVerificationDTO(String requestId, String otpCode) {
        this.requestId = requestId;
        this.otpCode = otpCode;
    }

    /**
     * Get the request ID
     * 
     * @return the request ID that identifies the OTP verification session
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Set the request ID
     * 
     * @param requestId the request ID that identifies the OTP verification session
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Get the OTP code
     * 
     * @return the 6-digit OTP code submitted for verification
     */
    public String getOtpCode() {
        return otpCode;
    }

    /**
     * Set the OTP code
     * 
     * @param otpCode the 6-digit OTP code submitted for verification
     */
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    @Override
    public String toString() {
        return "OTPVerificationDTO{" +
                "requestId='" + requestId + '\'' +
                ", otpCode='[PROTECTED]'" +
                '}';
    }
}