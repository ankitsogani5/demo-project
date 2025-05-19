package com.zensar.ankit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for OTP generation responses.
 * This class provides the client with a request ID that will be used for subsequent OTP verification,
 * along with status information about the OTP delivery.
 */
@Schema(description = "Response payload for OTP generation")
public class OTPResponseDTO {

    @Schema(description = "Unique identifier for the OTP verification session", 
            example = "a1b2c3d4-e5f6-7890", 
            required = true)
    private String requestId;

    @Schema(description = "Status of the OTP delivery", 
            example = "SUCCESS", 
            required = true)
    private String status;

    @Schema(description = "Additional information about the OTP delivery", 
            example = "OTP sent successfully to your mobile number", 
            required = true)
    private String message;

    /**
     * Default constructor
     */
    public OTPResponseDTO() {
    }

    /**
     * Parameterized constructor
     * 
     * @param requestId Unique identifier for the OTP verification session
     * @param status Status of the OTP delivery
     * @param message Additional information about the OTP delivery
     */
    public OTPResponseDTO(String requestId, String status, String message) {
        this.requestId = requestId;
        this.status = status;
        this.message = message;
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
     * Get the status
     * 
     * @return the status of the OTP delivery
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the status
     * 
     * @param status the status of the OTP delivery
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the message
     * 
     * @return additional information about the OTP delivery
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the message
     * 
     * @param message additional information about the OTP delivery
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "OTPResponseDTO{" +
                "requestId='" + requestId + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}