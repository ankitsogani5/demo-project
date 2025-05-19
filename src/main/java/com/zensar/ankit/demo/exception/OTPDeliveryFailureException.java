package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the system fails to deliver an OTP via SMS due to issues with the SMS gateway or other delivery mechanisms.
 * This exception is used to indicate failures in the OTP delivery process.
 * It returns HTTP status code 503 (Service Unavailable) and includes a specific error code.
 */
public class OTPDeliveryFailureException extends OTPException {

    private static final long serialVersionUID = 1L;
    
    /**
     * Standardized error code for SMS delivery failure scenario.
     */
    public static final String ERROR_CODE = "OTP_DELIVERY_FAILURE";
    
    /**
     * HTTP status code to be returned (503 Service Unavailable)
     */
    private static final HttpStatus HTTP_STATUS = HttpStatus.SERVICE_UNAVAILABLE;
    
    /**
     * Default error message for SMS delivery failure scenario.
     */
    private static final String DEFAULT_MESSAGE = "Failed to deliver OTP via SMS. The SMS service is currently unavailable.";
    
    /**
     * Default suggested action for SMS delivery failure scenario.
     */
    private static final String SUGGESTED_ACTION = "Please try an alternate verification method or try again later.";

    /**
     * Constructs a new OTPDeliveryFailureException with the default message.
     */
    public OTPDeliveryFailureException() {
        super(ERROR_CODE, DEFAULT_MESSAGE, HTTP_STATUS);
    }

    /**
     * Constructs a new OTPDeliveryFailureException with a custom message.
     * 
     * @param message The custom error message
     */
    public OTPDeliveryFailureException(String message) {
        super(ERROR_CODE, message, HTTP_STATUS);
    }

    /**
     * Constructs a new OTPDeliveryFailureException with a custom message and cause.
     * 
     * @param message The custom error message
     * @param cause The cause of the exception
     */
    public OTPDeliveryFailureException(String message, Throwable cause) {
        super(ERROR_CODE, message, HTTP_STATUS, cause);
    }
    
    /**
     * Constructs a new OTPDeliveryFailureException with the default message and additional details.
     * 
     * @param details Additional details about the delivery failure
     */
    public OTPDeliveryFailureException(Object details) {
        super(ERROR_CODE, DEFAULT_MESSAGE, HTTP_STATUS, details);
    }
    
    /**
     * Constructs a new OTPDeliveryFailureException with a custom message and additional details.
     * 
     * @param message The custom error message
     * @param details Additional details about the delivery failure
     */
    public OTPDeliveryFailureException(String message, Object details) {
        super(ERROR_CODE, message, HTTP_STATUS, details);
    }
    
    /**
     * Constructs a new OTPDeliveryFailureException with a custom message, cause, and additional details.
     * 
     * @param message The custom error message
     * @param cause The cause of the exception
     * @param details Additional details about the delivery failure
     */
    public OTPDeliveryFailureException(String message, Throwable cause, Object details) {
        super(ERROR_CODE, message, HTTP_STATUS, details, cause);
    }

    /**
     * Constructs a new OTPDeliveryFailureException with the default message and the specified mobile number.
     *
     * @param mobileNumber the mobile number for which SMS delivery failed
     * @return a new OTPDeliveryFailureException with a message including the mobile number
     */
    public static OTPDeliveryFailureException forMobileNumber(String mobileNumber) {
        return new OTPDeliveryFailureException(
            "Failed to deliver OTP via SMS to mobile number: " + mobileNumber + ". " + SUGGESTED_ACTION
        );
    }

    /**
     * Constructs a new OTPDeliveryFailureException with the default message, specified mobile number, and cause.
     *
     * @param mobileNumber the mobile number for which SMS delivery failed
     * @param cause The cause of the exception
     * @return a new OTPDeliveryFailureException with a message including the mobile number and the cause
     */
    public static OTPDeliveryFailureException forMobileNumber(String mobileNumber, Throwable cause) {
        return new OTPDeliveryFailureException(
            "Failed to deliver OTP via SMS to mobile number: " + mobileNumber + ". " + SUGGESTED_ACTION,
            cause
        );
    }

    /**
     * Constructs a new OTPDeliveryFailureException with the default message, specified mobile number, and additional details.
     *
     * @param mobileNumber the mobile number for which SMS delivery failed
     * @param details Additional details about the delivery failure
     * @return a new OTPDeliveryFailureException with a message including the mobile number and additional details
     */
    public static OTPDeliveryFailureException forMobileNumber(String mobileNumber, Object details) {
        return new OTPDeliveryFailureException(
            "Failed to deliver OTP via SMS to mobile number: " + mobileNumber + ". " + SUGGESTED_ACTION,
            details
        );
    }
    
    /**
     * Gets the suggested action for this exception.
     *
     * @return the suggested action to resolve the issue
     */
    public String getSuggestedAction() {
        return SUGGESTED_ACTION;
    }
}