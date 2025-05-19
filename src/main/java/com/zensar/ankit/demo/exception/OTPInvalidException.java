package com.zensar.ankit.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an incorrect OTP is provided during verification.
 * This exception is used to indicate that the submitted OTP does not match the one generated for the mobile number.
 * It includes a specific error code (OTP_INVALID) and HTTP status code (400 Bad Request).
 */
public class OTPInvalidException extends OTPException {

    private static final long serialVersionUID = 1L;
    private static final String ERROR_CODE = "OTP_INVALID";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String DEFAULT_MESSAGE = "The provided OTP is incorrect";
    
    private final int remainingAttempts;

    /**
     * Constructs a new OTPInvalidException with the default message and remaining attempts.
     *
     * @param remainingAttempts the number of verification attempts remaining before the OTP is invalidated
     */
    public OTPInvalidException(int remainingAttempts) {
        super(DEFAULT_MESSAGE, ERROR_CODE, HTTP_STATUS);
        this.remainingAttempts = remainingAttempts;
    }

    /**
     * Constructs a new OTPInvalidException with the specified message and remaining attempts.
     *
     * @param message the detail message
     * @param remainingAttempts the number of verification attempts remaining before the OTP is invalidated
     */
    public OTPInvalidException(String message, int remainingAttempts) {
        super(message, ERROR_CODE, HTTP_STATUS);
        this.remainingAttempts = remainingAttempts;
    }

    /**
     * Constructs a new OTPInvalidException with the specified message, cause, and remaining attempts.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param remainingAttempts the number of verification attempts remaining before the OTP is invalidated
     */
    public OTPInvalidException(String message, Throwable cause, int remainingAttempts) {
        super(message, ERROR_CODE, HTTP_STATUS, cause);
        this.remainingAttempts = remainingAttempts;
    }

    /**
     * Returns the number of verification attempts remaining before the OTP is invalidated.
     *
     * @return the remaining attempts count
     */
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    /**
     * Constructs a new OTPInvalidException with a message including the mobile number and remaining attempts.
     *
     * @param mobileNumber the mobile number for which the OTP verification failed
     * @param remainingAttempts the number of verification attempts remaining before the OTP is invalidated
     * @return a new OTPInvalidException with a message including the mobile number and remaining attempts
     */
    public static OTPInvalidException forMobileNumber(String mobileNumber, int remainingAttempts) {
        String message = String.format("Incorrect OTP provided for mobile number: %s. You have %d attempt(s) remaining.", 
                mobileNumber, remainingAttempts);
        return new OTPInvalidException(message, remainingAttempts);
    }

    /**
     * Constructs a new OTPInvalidException with a message including the reference ID and remaining attempts.
     *
     * @param referenceId the reference ID for which the OTP verification failed
     * @param remainingAttempts the number of verification attempts remaining before the OTP is invalidated
     * @return a new OTPInvalidException with a message including the reference ID and remaining attempts
     */
    public static OTPInvalidException forReferenceId(String referenceId, int remainingAttempts) {
        String message = String.format("Incorrect OTP provided for reference ID: %s. You have %d attempt(s) remaining.", 
                referenceId, remainingAttempts);
        return new OTPInvalidException(message, remainingAttempts);
    }
}