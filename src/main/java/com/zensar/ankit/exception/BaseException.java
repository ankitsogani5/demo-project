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
    
    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String correlationId;
    
    /**
     * Constructs a new BaseException with the specified error code, message, and HTTP status.
     * 
     * @param errorCode The standardized error code for this exception
     * @param message The detailed error message
     * @param httpStatus The HTTP status code to be returned to the client
     */
    public BaseException(String errorCode, String message, HttpStatus httpStatus) {
        this(errorCode, message, httpStatus, null);
    }
    
    /**
     * Constructs a new BaseException with the specified error code, message, HTTP status, and cause.
     * 
     * @param errorCode The standardized error code for this exception
     * @param message The detailed error message
     * @param httpStatus The HTTP status code to be returned to the client
     * @param cause The underlying cause of this exception
     */
    public BaseException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        this(errorCode, message, httpStatus, cause, null);
    }
    
    /**
     * Constructs a new BaseException with the specified error code, message, HTTP status, cause, and correlation ID.
     * 
     * @param errorCode The standardized error code for this exception
     * @param message The detailed error message
     * @param httpStatus The HTTP status code to be returned to the client
     * @param cause The underlying cause of this exception
     * @param correlationId The correlation ID for tracking this exception across system boundaries
     */
    public BaseException(String errorCode, String message, HttpStatus httpStatus, Throwable cause, String correlationId) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.correlationId = correlationId;
    }
    
    /**
     * Returns the standardized error code for this exception.
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
    
    /**
     * Returns the correlation ID for tracking this exception across system boundaries.
     * 
     * @return The correlation ID, or null if not set
     */
    public String getCorrelationId() {
        return correlationId;
    }
    
    /**
     * Returns a string representation of this exception, including the error code,
     * message, HTTP status, and correlation ID (if set).
     * 
     * @return A string representation of this exception
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(": ");
        sb.append("[ErrorCode: ").append(errorCode).append("] ");
        sb.append(getMessage());
        sb.append(" [Status: ").append(httpStatus).append("]");
        
        if (correlationId != null) {
            sb.append(" [CorrelationId: ").append(correlationId).append("]");
        }
        
        return sb.toString();
    }
}