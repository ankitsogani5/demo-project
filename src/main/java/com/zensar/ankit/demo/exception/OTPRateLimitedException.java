package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when too many OTP requests are made for a mobile number within a specific time period.
 * This exception is used to prevent abuse of the OTP generation system by limiting the number of
 * OTP requests to 5 per mobile number per hour.
 *
 * @see com.zensar.ankit.demo.advice.CustomExceptionHandler
 */
public class OTPRateLimitedException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    /**
     * The HTTP status code to be returned (429 Too Many Requests)
     */
    private final HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
    
    /**
     * The error code for this exception
     */
    private final String errorCode = "OTP_RATE_LIMITED";
    
    /**
     * Constructs a new OTPRateLimitedException with the default message.
     */
    public OTPRateLimitedException() {
        super("Rate limit exceeded. Maximum 5 OTP requests allowed per mobile number per hour.");
    }
    
    /**
     * Constructs a new OTPRateLimitedException with the specified message.
     *
     * @param message the detail message
     */
    public OTPRateLimitedException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new OTPRateLimitedException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public OTPRateLimitedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new OTPRateLimitedException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public OTPRateLimitedException(Throwable cause) {
        super("Rate limit exceeded. Maximum 5 OTP requests allowed per mobile number per hour.", cause);
    }
    
    /**
     * Returns the HTTP status code associated with this exception.
     *
     * @return the HTTP status code (429 Too Many Requests)
     */
    public HttpStatus getStatus() {
        return status;
    }
    
    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code (OTP_RATE_LIMITED)
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Returns a suggested action to resolve this exception.
     *
     * @return a string containing the suggested action
     */
    public String getSuggestedAction() {
        return "Please wait for the cooldown period to expire before requesting another OTP.";
    }
    
    /**
     * Returns the remaining time in minutes before a new OTP request can be made.
     * This is a placeholder method that should be implemented with actual time calculation.
     *
     * @param mobileNumber the mobile number for which to check the remaining time
     * @return the remaining time in minutes
     */
    public int getRemainingCooldownMinutes(String mobileNumber) {
        // This would be implemented to calculate the actual remaining time
        // based on the last request timestamp for the given mobile number
        return 60; // Default to maximum wait time
    }
}