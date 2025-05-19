package com.zensar.ankit.demo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Data Transfer Object for OTP generation responses.
 * This class provides the client with a request ID that will be used for subsequent OTP verification,
 * along with status information about the OTP delivery.
 */
@ApiModel(description = "Response object for OTP generation containing request ID and status information")
public class OTPResponseDTO {

    @ApiModelProperty(notes = "Unique identifier for the OTP verification session", required = true, example = "a1b2c3d4-e5f6-7890")
    private String requestId;

    @ApiModelProperty(notes = "Status of the OTP delivery (SUCCESS, PENDING, FAILED)", required = true, example = "SUCCESS")
    private String status;

    @ApiModelProperty(notes = "Additional information or error message", example = "OTP sent successfully to your mobile number")
    private String message;

    /**
     * Default constructor
     */
    public OTPResponseDTO() {
    }

    /**
     * Constructor with all fields
     * 
     * @param requestId Unique identifier for the OTP verification session
     * @param status Status of the OTP delivery
     * @param message Additional information or error message
     */
    public OTPResponseDTO(String requestId, String status, String message) {
        this.requestId = requestId;
        this.status = status;
        this.message = message;
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
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "OTPResponseDTO [requestId=" + requestId + ", status=" + status + ", message=" + message + "]";
    }
}