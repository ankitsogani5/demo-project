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
     * The error code associated with this exception.
     * Used for standardized error codes for OTP verification failures.
     */
    private final String errorCode;
    
    /**
     * The HTTP status code to be returned in the response.
     * Used to specify appropriate response status for different OTP error scenarios.
     */
    private final HttpStatus httpStatus;

    /**
     * Constructs a new OTPException with the specified message, error code, and HTTP status.
     *
     * @param message the detail message
     * @param errorCode the error code associated with this exception
     * @param httpStatus the HTTP status code to be returned in the response
     */
    public OTPException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Constructs a new OTPException with the specified message, error code, HTTP status, and cause.
     *
     * @param message the detail message
     * @param errorCode the error code associated with this exception
     * @param httpStatus the HTTP status code to be returned in the response
     * @param cause the cause of the exception
     */
    public OTPException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Constructs a new OTPException with the specified cause, error code, and HTTP status.
     * The detail message is set to the cause's detail message.
     *
     * @param cause the cause of the exception
     * @param errorCode the error code associated with this exception
     * @param httpStatus the HTTP status code to be returned in the response
     */
    public OTPException(Throwable cause, String errorCode, HttpStatus httpStatus) {
        super(cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the HTTP status code to be returned in the response.
     *
     * @return the HTTP status code
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}