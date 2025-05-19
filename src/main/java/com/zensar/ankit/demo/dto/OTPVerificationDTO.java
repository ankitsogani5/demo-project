package com.zensar.ankit.demo.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Data Transfer Object for OTP verification requests.
 * This class encapsulates the request ID and OTP code needed to verify a previously generated OTP.
 * It serves as the request payload for the OTP verification API endpoint.
 */
@ApiModel(description = "Data Transfer Object for OTP verification requests")
public class OTPVerificationDTO {

    @NotNull(message = "Request ID cannot be null")
    @ApiModelProperty(notes = "Unique identifier for the OTP verification session", required = true, example = "a1b2c3d4-e5f6-7890")
    private String requestId;

    @NotNull(message = "OTP code cannot be null")
    @Pattern(regexp = "[\\d]{6}", message = "OTP code should be a 6-digit number")
    @ApiModelProperty(notes = "6-digit OTP code received via SMS", required = true, example = "123456")
    private String otpCode;

    /**
     * Default constructor
     */
    public OTPVerificationDTO() {
        // Default constructor required for JSON deserialization
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

    @Override
    public String toString() {
        return "OTPVerificationDTO [requestId=" + requestId + ", otpCode=****]"; // Mask the OTP code for security
    }
}