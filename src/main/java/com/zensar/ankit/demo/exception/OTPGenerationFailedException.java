package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the system fails to generate an OTP due to internal errors.
 * This exception is used to indicate system-level failures in the OTP generation process.
 * It includes a specific error code (OTP_GENERATION_FAILED) and HTTP status code (500 Internal Server Error).
 */
public class OTPGenerationFailedException extends OTPException {

    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new OTPGenerationFailedException with the default message.
     */
    public OTPGenerationFailedException() {
        super("Failed to generate OTP due to an internal system error. Please contact support.", 
              "OTP_GENERATION_FAILED", 
              HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Constructs a new OTPGenerationFailedException with the specified message.
     * 
     * @param message the detail message
     */
    public OTPGenerationFailedException(String message) {
        super(message, "OTP_GENERATION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Constructs a new OTPGenerationFailedException with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public OTPGenerationFailedException(String message, Throwable cause) {
        super(message, cause, "OTP_GENERATION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Constructs a new OTPGenerationFailedException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public OTPGenerationFailedException(Throwable cause) {
        super("Failed to generate OTP due to an internal system error. Please contact support.", 
              cause, 
              "OTP_GENERATION_FAILED", 
              HttpStatus.INTERNAL_SERVER_ERROR);
    }
}