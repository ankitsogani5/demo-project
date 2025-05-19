package com.zensar.ankit.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception class for all application exceptions.
 * Provides standardized error handling with error codes and HTTP status codes.
 * This class serves as the foundation for all custom exceptions in the application.
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private final String errorCode;
    private final HttpStatus httpStatus;
    private final transient Object details;

    /**
     * Constructs a new BaseException with the specified error code, message, and HTTP status.
     *
     * @param errorCode   the error code for this exception
     * @param message     the detailed message
     * @param httpStatus  the HTTP status code to be returned to the client
     */
    public BaseException(String errorCode, String message, HttpStatus httpStatus) {
        this(errorCode, message, httpStatus, null);
    }

    /**
     * Constructs a new BaseException with the specified error code, message, HTTP status, and cause.
     *
     * @param errorCode   the error code for this exception
     * @param message     the detailed message
     * @param httpStatus  the HTTP status code to be returned to the client
     * @param cause       the cause of this exception
     */
    public BaseException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        this(errorCode, message, httpStatus, cause, null);
    }

    /**
     * Constructs a new BaseException with the specified error code, message, HTTP status, and additional details.
     *
     * @param errorCode   the error code for this exception
     * @param message     the detailed message
     * @param httpStatus  the HTTP status code to be returned to the client
     * @param details     additional details about the exception
     */
    public BaseException(String errorCode, String message, HttpStatus httpStatus, Object details) {
        this(errorCode, message, httpStatus, null, details);
    }

    /**
     * Constructs a new BaseException with all parameters.
     *
     * @param errorCode   the error code for this exception
     * @param message     the detailed message
     * @param httpStatus  the HTTP status code to be returned to the client
     * @param cause       the cause of this exception
     * @param details     additional details about the exception
     */
    public BaseException(String errorCode, String message, HttpStatus httpStatus, Throwable cause, Object details) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = details;
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
     * Returns the HTTP status code to be returned to the client.
     *
     * @return the HTTP status code
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    /**
     * Returns additional details about the exception.
     *
     * @return additional details, or null if none
     */
    public Object getDetails() {
        return details;
    }
}