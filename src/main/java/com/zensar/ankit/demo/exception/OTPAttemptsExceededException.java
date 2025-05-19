package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the maximum number of verification attempts (typically 3) for an OTP has been exceeded.
 * This exception is used to prevent brute force attacks on the OTP verification system.
 * 
 * The HTTP status code is set to 429 (Too Many Requests) to indicate that the client has sent too many requests
 * in a given amount of time, and should wait before trying again.
 */
public class OTPAttemptsExceededException extends OTPException {
    
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_MESSAGE = "Maximum verification attempts exceeded. Please wait and request a new OTP.";
    private static final String ERROR_CODE = "OTP_ATTEMPTS_EXCEEDED";
    private final int retryAfterSeconds;
    
    /**
     * Constructs a new OTPAttemptsExceededException with the default message and retry period.
     * 
     * @param retryAfterSeconds The number of seconds the client should wait before making a new request
     */
    public OTPAttemptsExceededException(int retryAfterSeconds) {
        super(DEFAULT_MESSAGE, ERROR_CODE, HttpStatus.TOO_MANY_REQUESTS);
        this.retryAfterSeconds = retryAfterSeconds;
    }
    
    /**
     * Constructs a new OTPAttemptsExceededException with a custom message and retry period.
     * 
     * @param message The custom error message
     * @param retryAfterSeconds The number of seconds the client should wait before making a new request
     */
    public OTPAttemptsExceededException(String message, int retryAfterSeconds) {
        super(message, ERROR_CODE, HttpStatus.TOO_MANY_REQUESTS);
        this.retryAfterSeconds = retryAfterSeconds;
    }
    
    /**
     * Returns the number of seconds the client should wait before making a new request.
     * This value can be used to set the Retry-After HTTP header in the response.
     * 
     * @return The retry after period in seconds
     */
    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}