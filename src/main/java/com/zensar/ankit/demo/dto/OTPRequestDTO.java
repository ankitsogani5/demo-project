package com.zensar.ankit.demo.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Data Transfer Object for OTP generation requests.
 * This class encapsulates the mobile number for which an OTP needs to be generated.
 * It serves as the request payload for the OTP generation API endpoint.
 */
@ApiModel(description = "Request model for OTP generation")
public class OTPRequestDTO {

    @NotNull(message = "Mobile number cannot be null")
    @Pattern(regexp = "[\\d]{10}", message = "Mobile number should be 10 digit number")
    @ApiModelProperty(notes = "Mobile number (10 digit number) for which OTP will be generated", required = true, example = "9876543210")
    private String mobileNumber;

    /**
     * Default constructor
     */
    public OTPRequestDTO() {
    }

    /**
     * Constructor with mobile number
     * 
     * @param mobileNumber the mobile number for OTP generation
     */
    public OTPRequestDTO(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * Get the mobile number
     * 
     * @return the mobile number
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * Set the mobile number
     * 
     * @param mobileNumber the mobile number to set
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}