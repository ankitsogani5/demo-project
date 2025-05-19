package com.zensar.ankit.exception;

import org.springframework.http.HttpStatus;

/**
 * Core exception class that serves as the foundation for all custom exceptions in the application.
 * This class extends RuntimeException and provides common functionality such as error code,
 * error message, and HTTP status code that can be used by exception handlers to generate
 * appropriate error responses.
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    /**
     * The error code associated with this exception
     */
    private final String errorCode;
    
    /**
     * The HTTP status code to be returned to the client
     */
    private final HttpStatus httpStatus;

    /**
     * Constructs a new BaseException with the specified error code, message, and HTTP status.
     *
     * @param errorCode The error code associated with this exception
     * @param message The detailed error message
     * @param httpStatus The HTTP status code to be returned to the client
     */
    public BaseException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Constructs a new BaseException with the specified error code, message, HTTP status, and cause.
     *
     * @param errorCode The error code associated with this exception
     * @param message The detailed error message
     * @param httpStatus The HTTP status code to be returned to the client
     * @param cause The cause of this exception
     */
    public BaseException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Constructs a new BaseException with the specified error code and message, using
     * HttpStatus.INTERNAL_SERVER_ERROR as the default HTTP status.
     *
     * @param errorCode The error code associated with this exception
     * @param message The detailed error message
     */
    public BaseException(String errorCode, String message) {
        this(errorCode, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Constructs a new BaseException with the specified error code, message, and cause,
     * using HttpStatus.INTERNAL_SERVER_ERROR as the default HTTP status.
     *
     * @param errorCode The error code associated with this exception
     * @param message The detailed error message
     * @param cause The cause of this exception
     */
    public BaseException(String errorCode, String message, Throwable cause) {
        this(errorCode, message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
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
     * Returns the HTTP status code to be returned to the client.
     *
     * @return The HTTP status code
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}