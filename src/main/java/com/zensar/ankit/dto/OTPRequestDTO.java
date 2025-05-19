package com.zensar.ankit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for OTP generation requests.
 * This class encapsulates the mobile number for which an OTP needs to be generated.
 * It serves as the request payload for the OTP generation API endpoint.
 */
@Schema(description = "Request payload for OTP generation")
public class OTPRequestDTO {

    @NotBlank(message = "Mobile number cannot be empty")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be a 10-digit number")
    @Schema(description = "10-digit mobile number for OTP delivery", 
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
     * @param mobileNumber 10-digit mobile number for OTP delivery
     */
    public OTPRequestDTO(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * Get the mobile number
     * 
     * @return the 10-digit mobile number for OTP delivery
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * Set the mobile number
     * 
     * @param mobileNumber the 10-digit mobile number for OTP delivery
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public String toString() {
        return "OTPRequestDTO{" +
                "mobileNumber='" + mobileNumber + '\'' +
                '}';
    }
}