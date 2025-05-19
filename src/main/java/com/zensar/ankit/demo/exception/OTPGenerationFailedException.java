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
     * Error code for OTP generation failure.
     */
    public static final String ERROR_CODE = "OTP_GENERATION_FAILED";
    
    /**
     * Default error message for OTP generation failure.
     */
    private static final String DEFAULT_MESSAGE = "Failed to generate OTP due to an internal system error";
    
    /**
     * Default suggested action for users encountering this error.
     */
    private static final String SUGGESTED_ACTION = "Please contact support for assistance";

    /**
     * Constructs a new OTPGenerationFailedException with the default message and HTTP status 500 (Internal Server Error).
     */
    public OTPGenerationFailedException() {
        super(ERROR_CODE, DEFAULT_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, 
              createDetails(null));
    }

    /**
     * Constructs a new OTPGenerationFailedException with a custom message and HTTP status 500 (Internal Server Error).
     *
     * @param message detailed error message
     */
    public OTPGenerationFailedException(String message) {
        super(ERROR_CODE, message, HttpStatus.INTERNAL_SERVER_ERROR, 
              createDetails(null));
    }

    /**
     * Constructs a new OTPGenerationFailedException with the default message, HTTP status 500 (Internal Server Error),
     * and the specified cause.
     *
     * @param cause the cause of this exception
     */
    public OTPGenerationFailedException(Throwable cause) {
        super(ERROR_CODE, DEFAULT_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, 
              createDetails(null), cause);
    }

    /**
     * Constructs a new OTPGenerationFailedException with a custom message, HTTP status 500 (Internal Server Error),
     * and the specified cause.
     *
     * @param message detailed error message
     * @param cause the cause of this exception
     */
    public OTPGenerationFailedException(String message, Throwable cause) {
        super(ERROR_CODE, message, HttpStatus.INTERNAL_SERVER_ERROR, 
              createDetails(null), cause);
    }
    
    /**
     * Constructs a new OTPGenerationFailedException with a custom message, HTTP status 500 (Internal Server Error),
     * and additional error details.
     *
     * @param message detailed error message
     * @param errorDetails specific details about the error that occurred
     */
    public OTPGenerationFailedException(String message, String errorDetails) {
        super(ERROR_CODE, message, HttpStatus.INTERNAL_SERVER_ERROR, 
              createDetails(errorDetails));
    }
    
    /**
     * Constructs a new OTPGenerationFailedException with a custom message, HTTP status 500 (Internal Server Error),
     * additional error details, and the specified cause.
     *
     * @param message detailed error message
     * @param errorDetails specific details about the error that occurred
     * @param cause the cause of this exception
     */
    public OTPGenerationFailedException(String message, String errorDetails, Throwable cause) {
        super(ERROR_CODE, message, HttpStatus.INTERNAL_SERVER_ERROR, 
              createDetails(errorDetails), cause);
    }
    
    /**
     * Creates a details object with suggested action and optional error details.
     * 
     * @param errorDetails specific details about the error that occurred (can be null)
     * @return a details object containing suggested action and optional error details
     */
    private static Object createDetails(String errorDetails) {
        if (errorDetails == null) {
            return java.util.Collections.singletonMap("suggestedAction", SUGGESTED_ACTION);
        } else {
            java.util.Map<String, String> details = new java.util.HashMap<>();
            details.put("suggestedAction", SUGGESTED_ACTION);
            details.put("errorDetails", errorDetails);
            return details;
        }
    }
}