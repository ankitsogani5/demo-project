package com.zensar.ankit.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for OTP generation requests.
 * This class encapsulates the mobile number for which an OTP needs to be generated.
 */
@Schema(description = "Request payload for OTP generation")
public class OTPRequestDTO {

    @NotBlank(message = "Mobile number cannot be empty")
    @Pattern(regexp = "[0-9]{10}", message = "Mobile number must be a 10-digit number")
    @Schema(description = "Mobile number for OTP delivery", 
            example = "9876543210", 
            required = true)
    private String mobileNumber;

    /**
     * Default constructor
     */
    public OTPRequestDTO() {
    }

    /**
     * Parameterized constructor
     * 
     * @param mobileNumber Mobile number for OTP delivery
     */
    public OTPRequestDTO(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * @return the mobileNumber
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * @param mobileNumber the mobileNumber to set
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}