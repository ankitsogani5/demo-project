package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an incorrect OTP is provided during verification.
 * This exception indicates that the submitted OTP does not match the one generated for the mobile number.
 */
public class OTPInvalidException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    /**
     * Error code for invalid OTP
     */
    private final String errorCode = "OTP_INVALID";
    
    /**
     * HTTP status code to be returned (400 Bad Request)
     */
    private final HttpStatus status = HttpStatus.BAD_REQUEST;
    
    /**
     * Number of verification attempts remaining before the OTP is invalidated
     */
    private final int remainingAttempts;
    
    /**
     * Constructs a new OTPInvalidException with the specified detail message and remaining attempts.
     *
     * @param message the detail message
     * @param remainingAttempts the number of verification attempts remaining
     */
    public OTPInvalidException(String message, int remainingAttempts) {
        super(message);
        this.remainingAttempts = remainingAttempts;
    }
    
    /**
     * Constructs a new OTPInvalidException with the specified detail message, cause, and remaining attempts.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param remainingAttempts the number of verification attempts remaining
     */
    public OTPInvalidException(String message, Throwable cause, int remainingAttempts) {
        super(message, cause);
        this.remainingAttempts = remainingAttempts;
    }
    
    /**
     * Returns the error code for this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Returns the HTTP status code to be used in the response.
     *
     * @return the HTTP status code
     */
    public HttpStatus getStatus() {
        return status;
    }
    
    /**
     * Returns the number of verification attempts remaining before the OTP is invalidated.
     *
     * @return the number of remaining attempts
     */
    public int getRemainingAttempts() {
        return remainingAttempts;
    }
}