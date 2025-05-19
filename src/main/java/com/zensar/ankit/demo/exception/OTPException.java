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
    
    private final String errorCode;
    private final HttpStatus httpStatus;
    
    /**
     * Constructs a new OTPException with the specified detail message, error code, and HTTP status.
     * 
     * @param message The detail message
     * @param errorCode The error code that identifies the specific error type
     * @param httpStatus The HTTP status code to be returned in the response
     */
    public OTPException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    /**
     * Constructs a new OTPException with the specified detail message, error code, HTTP status, and cause.
     * 
     * @param message The detail message
     * @param errorCode The error code that identifies the specific error type
     * @param httpStatus The HTTP status code to be returned in the response
     * @param cause The cause of the exception
     */
    public OTPException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    /**
     * Returns the error code associated with this exception.
     * 
     * @return The error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Returns the HTTP status code associated with this exception.
     * 
     * @return The HTTP status code
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}