package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an attempt is made to verify an OTP that has already been successfully verified.
 * This exception is used to prevent replay attacks where the same OTP is used multiple times.
 * It returns HTTP status code 400 (Bad Request) and includes a specific error code.
 */
public class OTPAlreadyVerifiedException extends OTPException {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_MESSAGE = "This OTP has already been verified";
    private static final String ERROR_CODE = "OTP_ALREADY_VERIFIED";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String SUGGESTED_ACTION = "Please proceed to the next step in the registration process";

    /**
     * Constructs a new OTPAlreadyVerifiedException with the default message.
     */
    public OTPAlreadyVerifiedException() {
        super(DEFAULT_MESSAGE, ERROR_CODE, HTTP_STATUS);
    }

    /**
     * Constructs a new OTPAlreadyVerifiedException with the specified message.
     *
     * @param message the detail message
     */
    public OTPAlreadyVerifiedException(String message) {
        super(message, ERROR_CODE, HTTP_STATUS);
    }

    /**
     * Constructs a new OTPAlreadyVerifiedException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public OTPAlreadyVerifiedException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE, HTTP_STATUS);
    }

    /**
     * Constructs a new OTPAlreadyVerifiedException with the default message and the specified mobile number.
     *
     * @param mobileNumber the mobile number for which the OTP has already been verified
     * @return a new OTPAlreadyVerifiedException with a message including the mobile number
     */
    public static OTPAlreadyVerifiedException forMobileNumber(String mobileNumber) {
        return new OTPAlreadyVerifiedException("OTP for mobile number: " + mobileNumber + 
                " has already been verified. " + SUGGESTED_ACTION);
    }

    /**
     * Constructs a new OTPAlreadyVerifiedException with the default message and the specified reference ID.
     *
     * @param referenceId the reference ID for which the OTP has already been verified
     * @return a new OTPAlreadyVerifiedException with a message including the reference ID
     */
    public static OTPAlreadyVerifiedException forReferenceId(String referenceId) {
        return new OTPAlreadyVerifiedException("OTP for reference ID: " + referenceId + 
                " has already been verified. " + SUGGESTED_ACTION);
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