package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when no OTP record is found for a given mobile number or reference ID during verification.
 * This exception indicates that the OTP being verified does not exist in the system.
 */
public class OTPNotFoundException extends OTPException {

    private static final long serialVersionUID = 1L;
    private static final String ERROR_CODE = "OTP_NOT_FOUND";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
    private static final String DEFAULT_MESSAGE = "No OTP found for the provided mobile number or reference ID";
    private static final String SUGGESTED_ACTION = "Please generate a new OTP and try again";

    /**
     * Constructs a new OTPNotFoundException with the default message.
     */
    public OTPNotFoundException() {
        super(DEFAULT_MESSAGE, ERROR_CODE, HTTP_STATUS);
    }

    /**
     * Constructs a new OTPNotFoundException with the specified message.
     *
     * @param message the detail message
     */
    public OTPNotFoundException(String message) {
        super(message, ERROR_CODE, HTTP_STATUS);
    }

    /**
     * Constructs a new OTPNotFoundException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public OTPNotFoundException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE, HTTP_STATUS);
    }

    /**
     * Constructs a new OTPNotFoundException with the default message and the specified mobile number.
     *
     * @param mobileNumber the mobile number for which no OTP was found
     * @return a new OTPNotFoundException with a message including the mobile number
     */
    public static OTPNotFoundException forMobileNumber(String mobileNumber) {
        return new OTPNotFoundException("No OTP found for mobile number: " + mobileNumber + ". " + SUGGESTED_ACTION);
    }

    /**
     * Constructs a new OTPNotFoundException with the default message and the specified reference ID.
     *
     * @param referenceId the reference ID for which no OTP was found
     * @return a new OTPNotFoundException with a message including the reference ID
     */
    public static OTPNotFoundException forReferenceId(String referenceId) {
        return new OTPNotFoundException("No OTP found for reference ID: " + referenceId + ". " + SUGGESTED_ACTION);
    }

    /**
     * Returns the suggested action for this exception.
     *
     * @return the suggested action to resolve the issue
     */
    public String getSuggestedAction() {
        return SUGGESTED_ACTION;
    }
}