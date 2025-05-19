package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the maximum number of verification attempts (typically 3) for an OTP has been exceeded.
 * This exception is used to prevent brute force attacks on the OTP verification system.
 * It returns HTTP status code 429 (Too Many Requests) and includes a specific error code.
 */
public class OTPAttemptsExceededException extends OTPException {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_MESSAGE = "Maximum verification attempts exceeded. Please wait and request a new OTP.";
    private static final String ERROR_CODE = "OTP_ATTEMPTS_EXCEEDED";
    private static final HttpStatus HTTP_STATUS = HttpStatus.TOO_MANY_REQUESTS;
    
    private final int retryAfterSeconds;

    /**
     * Constructs a new OTPAttemptsExceededException with the default message and retry period.
     */
    public OTPAttemptsExceededException() {
        this(DEFAULT_MESSAGE, 300); // Default 5 minutes (300 seconds) retry period
    }

    /**
     * Constructs a new OTPAttemptsExceededException with the default message and specified retry period.
     * 
     * @param retryAfterSeconds The number of seconds after which the client can retry
     */
    public OTPAttemptsExceededException(int retryAfterSeconds) {
        this(DEFAULT_MESSAGE, retryAfterSeconds);
    }

    /**
     * Constructs a new OTPAttemptsExceededException with a custom message and default retry period.
     * 
     * @param message The custom error message
     */
    public OTPAttemptsExceededException(String message) {
        this(message, 300); // Default 5 minutes (300 seconds) retry period
    }

    /**
     * Constructs a new OTPAttemptsExceededException with a custom message and specified retry period.
     * 
     * @param message The custom error message
     * @param retryAfterSeconds The number of seconds after which the client can retry
     */
    public OTPAttemptsExceededException(String message, int retryAfterSeconds) {
        super(message, ERROR_CODE, HTTP_STATUS);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    /**
     * Constructs a new OTPAttemptsExceededException with a custom message, cause, and default retry period.
     * 
     * @param message The custom error message
     * @param cause The cause of the exception
     */
    public OTPAttemptsExceededException(String message, Throwable cause) {
        this(message, cause, 300); // Default 5 minutes (300 seconds) retry period
    }

    /**
     * Constructs a new OTPAttemptsExceededException with a custom message, cause, and specified retry period.
     * 
     * @param message The custom error message
     * @param cause The cause of the exception
     * @param retryAfterSeconds The number of seconds after which the client can retry
     */
    public OTPAttemptsExceededException(String message, Throwable cause, int retryAfterSeconds) {
        super(message, cause, ERROR_CODE, HTTP_STATUS);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    /**
     * Gets the number of seconds after which the client can retry the OTP verification.
     * This value should be used to set the Retry-After HTTP header in the response.
     * 
     * @return The retry after period in seconds
     */
    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}