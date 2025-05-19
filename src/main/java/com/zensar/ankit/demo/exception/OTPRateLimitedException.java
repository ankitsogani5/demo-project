package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when too many OTP requests are made for a mobile number within a specific time period (typically 5 requests per hour).
 * This exception is used to prevent abuse of the OTP generation system.
 * It returns HTTP status code 429 (Too Many Requests) and includes a specific error code.
 */
public class OTPRateLimitedException extends OTPException {

    private static final long serialVersionUID = 1L;
    
    /**
     * Standardized error code for rate limiting scenario.
     */
    public static final String ERROR_CODE = "OTP_RATE_LIMITED";
    
    /**
     * HTTP status code to be returned (429 Too Many Requests)
     */
    private static final HttpStatus HTTP_STATUS = HttpStatus.TOO_MANY_REQUESTS;
    
    /**
     * Default error message for rate limiting scenario.
     */
    private static final String DEFAULT_MESSAGE = "Rate limit exceeded. Too many OTP requests for this mobile number.";
    
    /**
     * Default suggested action for rate limiting scenario.
     */
    private static final String SUGGESTED_ACTION = "Please wait before requesting another OTP.";
    
    /**
     * Number of seconds after which the client can retry requesting an OTP.
     */
    private final int retryAfterSeconds;

    /**
     * Constructs a new OTPRateLimitedException with the default message and a default retry period of 1 hour (3600 seconds).
     */
    public OTPRateLimitedException() {
        this(DEFAULT_MESSAGE, 3600); // Default 1 hour (3600 seconds) retry period
    }

    /**
     * Constructs a new OTPRateLimitedException with the default message and specified retry period.
     * 
     * @param retryAfterSeconds The number of seconds after which the client can retry
     */
    public OTPRateLimitedException(int retryAfterSeconds) {
        this(DEFAULT_MESSAGE, retryAfterSeconds);
    }

    /**
     * Constructs a new OTPRateLimitedException with a custom message and default retry period.
     * 
     * @param message The custom error message
     */
    public OTPRateLimitedException(String message) {
        this(message, 3600); // Default 1 hour (3600 seconds) retry period
    }

    /**
     * Constructs a new OTPRateLimitedException with a custom message and specified retry period.
     * 
     * @param message The custom error message
     * @param retryAfterSeconds The number of seconds after which the client can retry
     */
    public OTPRateLimitedException(String message, int retryAfterSeconds) {
        super(ERROR_CODE, message, HTTP_STATUS);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    /**
     * Constructs a new OTPRateLimitedException with a custom message, cause, and default retry period.
     * 
     * @param message The custom error message
     * @param cause The cause of the exception
     */
    public OTPRateLimitedException(String message, Throwable cause) {
        this(message, cause, 3600); // Default 1 hour (3600 seconds) retry period
    }

    /**
     * Constructs a new OTPRateLimitedException with a custom message, cause, and specified retry period.
     * 
     * @param message The custom error message
     * @param cause The cause of the exception
     * @param retryAfterSeconds The number of seconds after which the client can retry
     */
    public OTPRateLimitedException(String message, Throwable cause, int retryAfterSeconds) {
        super(ERROR_CODE, message, HTTP_STATUS, cause);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    /**
     * Constructs a new OTPRateLimitedException with the default message and the specified mobile number.
     *
     * @param mobileNumber the mobile number that has been rate limited
     * @param retryAfterSeconds The number of seconds after which the client can retry
     * @return a new OTPRateLimitedException with a message including the mobile number
     */
    public static OTPRateLimitedException forMobileNumber(String mobileNumber, int retryAfterSeconds) {
        return new OTPRateLimitedException(
            "Rate limit exceeded for mobile number: " + mobileNumber + ". " + SUGGESTED_ACTION, 
            retryAfterSeconds
        );
    }

    /**
     * Gets the number of seconds after which the client can retry requesting an OTP.
     * This value should be used to set the Retry-After HTTP header in the response.
     * 
     * @return The retry after period in seconds
     */
    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
    
    /**
     * Gets the suggested action for this exception.
     *
     * @return the suggested action to resolve the issue
     */
    public String getSuggestedAction() {
        return SUGGESTED_ACTION + " Try again after " + retryAfterSeconds + " seconds.";
    }
}