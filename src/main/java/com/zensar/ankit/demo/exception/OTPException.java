package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception class for all OTP-related exceptions in the application.
 * This class extends RuntimeException and serves as the parent class for all specific OTP exception types.
 * It provides common functionality such as error code, error message, and HTTP status code that can be used
 * by the CustomExceptionHandler to generate appropriate error responses.
 */
public class OTPException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    /**
     * Standardized error code for OTP verification failures.
     * Possible values include:
     * - OTP_EXPIRED: OTP has exceeded its TTL
     * - OTP_INVALID: Incorrect OTP provided
     * - OTP_ATTEMPTS_EXCEEDED: Maximum verification attempts reached
     * - OTP_ALREADY_VERIFIED: OTP has already been successfully verified
     * - OTP_GENERATION_FAILED: System failed to generate OTP
     * - OTP_DELIVERY_FAILURE: SMS delivery service unavailable
     * - OTP_RATE_LIMITED: Too many OTP requests in time period
     */
    private final String errorCode;
    
    /**
     * HTTP status code to be returned in the response.
     * Common values for OTP exceptions:
     * - 400 Bad Request (OTP_EXPIRED, OTP_INVALID, OTP_ALREADY_VERIFIED)
     * - 429 Too Many Requests (OTP_ATTEMPTS_EXCEEDED, OTP_RATE_LIMITED)
     * - 500 Internal Server Error (OTP_GENERATION_FAILED)
     * - 503 Service Unavailable (OTP_DELIVERY_FAILURE)
     */
    private final HttpStatus status;
    
    /**
     * Additional details about the exception that can be included in the response.
     * This may include information such as:
     * - Remaining attempts allowed
     * - Time until next attempt is allowed
     * - Suggested user action
     */
    private final Object details;

    /**
     * Constructs a new OTP exception with the specified error code, message, and HTTP status.
     *
     * @param errorCode standardized error code for the exception
     * @param message detailed error message
     * @param status HTTP status code to be returned in the response
     */
    public OTPException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.details = null;
    }

    /**
     * Constructs a new OTP exception with the specified error code, message, HTTP status, and additional details.
     *
     * @param errorCode standardized error code for the exception
     * @param message detailed error message
     * @param status HTTP status code to be returned in the response
     * @param details additional information about the exception
     */
    public OTPException(String errorCode, String message, HttpStatus status, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.details = details;
    }

    /**
     * Constructs a new OTP exception with the specified error code, message, HTTP status, and cause.
     *
     * @param errorCode standardized error code for the exception
     * @param message detailed error message
     * @param status HTTP status code to be returned in the response
     * @param cause the cause of this exception
     */
    public OTPException(String errorCode, String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = status;
        this.details = null;
    }

    /**
     * Constructs a new OTP exception with the specified error code, message, HTTP status, additional details, and cause.
     *
     * @param errorCode standardized error code for the exception
     * @param message detailed error message
     * @param status HTTP status code to be returned in the response
     * @param details additional information about the exception
     * @param cause the cause of this exception
     */
    public OTPException(String errorCode, String message, HttpStatus status, Object details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = status;
        this.details = details;
    }

    /**
     * Gets the standardized error code for this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the HTTP status code to be returned in the response.
     *
     * @return the HTTP status
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * Gets additional details about the exception.
     *
     * @return additional details, or null if none
     */
    public Object getDetails() {
        return details;
    }
}