package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when no OTP record is found for a given mobile number or reference ID during verification.
 * This exception is used to indicate that the OTP being verified does not exist in the system.
 */
public class OTPNotFoundException extends OTPException {

    private static final long serialVersionUID = 1L;
    
    /**
     * Standardized error code for OTP not found scenario.
     */
    public static final String ERROR_CODE = "OTP_NOT_FOUND";
    
    /**
     * Default error message for OTP not found scenario.
     */
    private static final String DEFAULT_MESSAGE = "No OTP found for the provided reference. Please generate a new OTP.";

    /**
     * Constructs a new OTPNotFoundException with the default message and HTTP status 404 Not Found.
     */
    public OTPNotFoundException() {
        super(ERROR_CODE, DEFAULT_MESSAGE, HttpStatus.NOT_FOUND);
    }

    /**
     * Constructs a new OTPNotFoundException with a custom message and HTTP status 404 Not Found.
     *
     * @param message detailed error message
     */
    public OTPNotFoundException(String message) {
        super(ERROR_CODE, message, HttpStatus.NOT_FOUND);
    }

    /**
     * Constructs a new OTPNotFoundException with a custom message, HTTP status 404 Not Found,
     * and additional details.
     *
     * @param message detailed error message
     * @param details additional information about the exception
     */
    public OTPNotFoundException(String message, Object details) {
        super(ERROR_CODE, message, HttpStatus.NOT_FOUND, details);
    }

    /**
     * Constructs a new OTPNotFoundException with a custom message, HTTP status 404 Not Found,
     * and the cause of the exception.
     *
     * @param message detailed error message
     * @param cause the cause of this exception
     */
    public OTPNotFoundException(String message, Throwable cause) {
        super(ERROR_CODE, message, HttpStatus.NOT_FOUND, cause);
    }

    /**
     * Constructs a new OTPNotFoundException with a custom message, HTTP status 404 Not Found,
     * additional details, and the cause of the exception.
     *
     * @param message detailed error message
     * @param details additional information about the exception
     * @param cause the cause of this exception
     */
    public OTPNotFoundException(String message, Object details, Throwable cause) {
        super(ERROR_CODE, message, HttpStatus.NOT_FOUND, details, cause);
    }
}