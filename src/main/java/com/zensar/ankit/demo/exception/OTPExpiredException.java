package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an OTP has exceeded its time-to-live (TTL) period, typically 10 minutes from generation.
 * This exception is used in the OTP verification process to indicate that the submitted OTP is no longer valid due to expiration.
 * It includes a specific error code (OTP_EXPIRED) and HTTP status code (410 Gone).
 */
public class OTPExpiredException extends OTPException {

    private static final long serialVersionUID = 1L;
    private static final String ERROR_CODE = "OTP_EXPIRED";
    private static final HttpStatus HTTP_STATUS = HttpStatus.GONE;
    private static final String DEFAULT_MESSAGE = "The OTP has expired. Please request a new OTP.";
    
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
        super(message, ERROR_CODE, HTTP_STATUS, cause);
    }

    /**
     * Constructs a new OTPExpiredException with a message including the mobile number.
     *
     * @param mobileNumber the mobile number for which the OTP has expired
     * @return a new OTPExpiredException with a message including the mobile number
     */
    public static OTPExpiredException forMobileNumber(String mobileNumber) {
        String message = String.format("The OTP for mobile number %s has expired. Please request a new OTP.", 
                mobileNumber);
        return new OTPExpiredException(message);
    }

    /**
     * Constructs a new OTPExpiredException with a message including the reference ID.
     *
     * @param referenceId the reference ID for which the OTP has expired
     * @return a new OTPExpiredException with a message including the reference ID
     */
    public static OTPExpiredException forReferenceId(String referenceId) {
        String message = String.format("The OTP for reference ID %s has expired. Please request a new OTP.", 
                referenceId);
        return new OTPExpiredException(message);
    }
}