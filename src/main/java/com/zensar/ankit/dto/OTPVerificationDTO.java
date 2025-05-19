package com.zensar.ankit.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for OTP verification requests.
 * This class encapsulates the request ID and OTP code needed to verify a previously generated OTP.
 */
@Schema(description = "Request payload for OTP verification")
public class OTPVerificationDTO {

    @NotBlank(message = "Request ID cannot be empty")
    @Schema(description = "Unique request ID received during OTP generation", 
            example = "abc123def456", 
            required = true)
    private String requestId;

    @NotBlank(message = "OTP code cannot be empty")
    @Pattern(regexp = "[0-9]{6}", message = "OTP code must be a 6-digit number")
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
     * @param requestId Unique request ID received during OTP generation
     * @param otpCode   6-digit OTP code received via SMS
     */
    public OTPVerificationDTO(String requestId, String otpCode) {
        this.requestId = requestId;
        this.otpCode = otpCode;
    }

    /**
     * @return the requestId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * @return the otpCode
     */
    public String getOtpCode() {
        return otpCode;
    }

    /**
     * @param otpCode the otpCode to set
     */
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}