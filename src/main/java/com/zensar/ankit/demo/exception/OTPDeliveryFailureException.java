package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the system fails to deliver an OTP via SMS due to issues 
 * with the SMS gateway or other delivery mechanisms.
 * <p>
 * This exception is used to indicate failures in the OTP delivery process and 
 * returns an HTTP 503 Service Unavailable status code to the client.
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class OTPDeliveryFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    /**
     * Error code for OTP delivery failure
     */
    public static final String ERROR_CODE = "OTP_DELIVERY_FAILURE";
    
    /**
     * HTTP status code for service unavailable
     */
    private final HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
    
    /**
     * Constructs a new OTPDeliveryFailureException with the default message.
     */
    public OTPDeliveryFailureException() {
        super("Failed to deliver OTP via SMS. Please try again later or use an alternate verification method.");
    }
    
    /**
     * Constructs a new OTPDeliveryFailureException with the specified message.
     *
     * @param message the detail message
     */
    public OTPDeliveryFailureException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new OTPDeliveryFailureException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public OTPDeliveryFailureException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new OTPDeliveryFailureException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public OTPDeliveryFailureException(Throwable cause) {
        super("Failed to deliver OTP via SMS. Please try again later or use an alternate verification method.", cause);
    }
    
    /**
     * Returns the error code for this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return ERROR_CODE;
    }
    
    /**
     * Returns the HTTP status code for this exception.
     *
     * @return the HTTP status code
     */
    public HttpStatus getStatus() {
        return status;
    }
}