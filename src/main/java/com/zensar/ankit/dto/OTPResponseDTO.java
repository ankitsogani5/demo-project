package com.zensar.ankit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for OTP generation responses.
 * This class provides the client with a request ID that will be used for subsequent OTP verification,
 * along with status information about the OTP delivery.
 */
@Schema(description = "Response payload for OTP generation")
public class OTPResponseDTO {

    @Schema(description = "Unique request ID to be used for OTP verification", 
            example = "abc123def456", 
            required = true)
    private String requestId;

    @Schema(description = "Status of the OTP delivery", 
            example = "SENT", 
            required = true)
    private String status;

    @Schema(description = "Additional information about the OTP delivery", 
            example = "OTP sent successfully", 
            required = true)
    private String message;

    @Schema(description = "Time in seconds until the OTP expires", 
            example = "600", 
            required = true)
    private Integer expiresIn;

    /**
     * Default constructor
     */
    public OTPResponseDTO() {
    }

    /**
     * Parameterized constructor
     * 
     * @param requestId Unique request ID to be used for OTP verification
     * @param status    Status of the OTP delivery
     * @param message   Additional information about the OTP delivery
     * @param expiresIn Time in seconds until the OTP expires
     */
    public OTPResponseDTO(String requestId, String status, String message, Integer expiresIn) {
        this.requestId = requestId;
        this.status = status;
        this.message = message;
        this.expiresIn = expiresIn;
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

    /**
     * @return the expiresIn
     */
    public Integer getExpiresIn() {
        return expiresIn;
    }

    /**
     * @param expiresIn the expiresIn to set
     */
    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}