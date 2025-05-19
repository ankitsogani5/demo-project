package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an OTP has exceeded its time-to-live (TTL) period, typically 10 minutes from generation.
 * This exception is used in the OTP verification process to indicate that the submitted OTP is no longer valid due to expiration.
 */
public class OTPExpiredException extends OTPException {

    private static final long serialVersionUID = 1L;
    private static final String ERROR_CODE = "OTP_EXPIRED";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String DEFAULT_MESSAGE = "The OTP has expired";
    private static final String SUGGESTED_ACTION = "Please request a new OTP and try again";

    /**
     * Constructs a new OTPExpiredException with the default message.
     */
    public OTPExpiredException() {
        super(DEFAULT_MESSAGE, ERROR_CODE, HTTP_STATUS);
    }

    /**
     * Constructs a new OTPExpiredException with the specified message.
     *
     * @param message the detail message
     */
    public OTPExpiredException(String message) {
        super(message, ERROR_CODE, HTTP_STATUS);
    }

    /**
     * Constructs a new OTPExpiredException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public OTPExpiredException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE, HTTP_STATUS);
    }

    /**
     * Constructs a new OTPExpiredException with the default message and the specified mobile number.
     *
     * @param mobileNumber the mobile number for which the OTP has expired
     * @return a new OTPExpiredException with a message including the mobile number
     */
    public static OTPExpiredException forMobileNumber(String mobileNumber) {
        return new OTPExpiredException("OTP for mobile number: " + mobileNumber + " has expired. " + SUGGESTED_ACTION);
    }

    /**
     * Constructs a new OTPExpiredException with the default message and the specified reference ID.
     *
     * @param referenceId the reference ID for which the OTP has expired
     * @return a new OTPExpiredException with a message including the reference ID
     */
    public static OTPExpiredException forReferenceId(String referenceId) {
        return new OTPExpiredException("OTP for reference ID: " + referenceId + " has expired. " + SUGGESTED_ACTION);
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