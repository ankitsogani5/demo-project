package com.zensar.ankit.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception class for all OTP-related exceptions in the application.
 * This class extends BaseException and serves as the parent class for all specific OTP exception types.
 * It provides OTP-specific functionality such as remaining attempts, reference ID, and suggested actions
 * that can be used by the CustomExceptionHandler to generate appropriate error responses for OTP verification failures.
 */
public class OTPBaseException extends BaseException {

    private static final long serialVersionUID = 1L;
    
    /**
     * Number of verification attempts remaining before account lockout or cooldown period.
     * A value of 0 indicates no remaining attempts.
     * A value of -1 indicates unlimited attempts or not applicable.
     */
    private final int remainingAttempts;
    
    /**
     * Unique reference ID for the OTP request.
     * This can be used for tracking and correlating OTP requests across multiple systems.
     */
    private final String referenceId;
    
    /**
     * Suggested action for the user to resolve the issue.
     * This provides guidance on what the user should do next to proceed with verification.
     */
    private final String suggestedAction;
    
    /**
     * Constructs a new OTPBaseException with the specified error code, message, and HTTP status.
     *
     * @param errorCode standardized error code for the exception
     * @param message detailed error message
     * @param httpStatus HTTP status code to be returned in the response
     */
    public OTPBaseException(String errorCode, String message, HttpStatus httpStatus) {
        this(errorCode, message, httpStatus, -1, null, null);
    }
    
    /**
     * Constructs a new OTPBaseException with the specified error code, message, HTTP status, and cause.
     *
     * @param errorCode standardized error code for the exception
     * @param message detailed error message
     * @param httpStatus HTTP status code to be returned in the response
     * @param cause the cause of this exception
     */
    public OTPBaseException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        this(errorCode, message, httpStatus, -1, null, null, cause);
    }
    
    /**
     * Constructs a new OTPBaseException with the specified error code, message, HTTP status, and remaining attempts.
     *
     * @param errorCode standardized error code for the exception
     * @param message detailed error message
     * @param httpStatus HTTP status code to be returned in the response
     * @param remainingAttempts number of verification attempts remaining
     */
    public OTPBaseException(String errorCode, String message, HttpStatus httpStatus, int remainingAttempts) {
        this(errorCode, message, httpStatus, remainingAttempts, null, null);
    }
    
    /**
     * Constructs a new OTPBaseException with the specified error code, message, HTTP status,
     * remaining attempts, reference ID, and suggested action.
     *
     * @param errorCode standardized error code for the exception
     * @param message detailed error message
     * @param httpStatus HTTP status code to be returned in the response
     * @param remainingAttempts number of verification attempts remaining
     * @param referenceId unique reference ID for the OTP request
     * @param suggestedAction suggested action for the user to resolve the issue
     */
    public OTPBaseException(String errorCode, String message, HttpStatus httpStatus, 
                           int remainingAttempts, String referenceId, String suggestedAction) {
        super(errorCode, message, httpStatus);
        this.remainingAttempts = remainingAttempts;
        this.referenceId = referenceId;
        this.suggestedAction = suggestedAction;
    }
    
    /**
     * Constructs a new OTPBaseException with the specified error code, message, HTTP status,
     * remaining attempts, reference ID, suggested action, and cause.
     *
     * @param errorCode standardized error code for the exception
     * @param message detailed error message
     * @param httpStatus HTTP status code to be returned in the response
     * @param remainingAttempts number of verification attempts remaining
     * @param referenceId unique reference ID for the OTP request
     * @param suggestedAction suggested action for the user to resolve the issue
     * @param cause the cause of this exception
     */
    public OTPBaseException(String errorCode, String message, HttpStatus httpStatus, 
                           int remainingAttempts, String referenceId, String suggestedAction, Throwable cause) {
        super(errorCode, message, httpStatus, cause);
        this.remainingAttempts = remainingAttempts;
        this.referenceId = referenceId;
        this.suggestedAction = suggestedAction;
    }
    
    /**
     * Constructs a new OTPBaseException with the specified error code, message, HTTP status,
     * remaining attempts, reference ID, suggested action, and additional details.
     *
     * @param errorCode standardized error code for the exception
     * @param message detailed error message
     * @param httpStatus HTTP status code to be returned in the response
     * @param remainingAttempts number of verification attempts remaining
     * @param referenceId unique reference ID for the OTP request
     * @param suggestedAction suggested action for the user to resolve the issue
     * @param details additional details about the exception
     */
    public OTPBaseException(String errorCode, String message, HttpStatus httpStatus, 
                           int remainingAttempts, String referenceId, String suggestedAction, Object details) {
        super(errorCode, message, httpStatus, details);
        this.remainingAttempts = remainingAttempts;
        this.referenceId = referenceId;
        this.suggestedAction = suggestedAction;
    }
    
    /**
     * Constructs a new OTPBaseException with all parameters.
     *
     * @param errorCode standardized error code for the exception
     * @param message detailed error message
     * @param httpStatus HTTP status code to be returned in the response
     * @param remainingAttempts number of verification attempts remaining
     * @param referenceId unique reference ID for the OTP request
     * @param suggestedAction suggested action for the user to resolve the issue
     * @param cause the cause of this exception
     * @param details additional details about the exception
     */
    public OTPBaseException(String errorCode, String message, HttpStatus httpStatus, 
                           int remainingAttempts, String referenceId, String suggestedAction, 
                           Throwable cause, Object details) {
        super(errorCode, message, httpStatus, cause, details);
        this.remainingAttempts = remainingAttempts;
        this.referenceId = referenceId;
        this.suggestedAction = suggestedAction;
    }
    
    /**
     * Gets the number of verification attempts remaining.
     *
     * @return the number of remaining attempts, 0 for no remaining attempts, or -1 if not applicable
     */
    public int getRemainingAttempts() {
        return remainingAttempts;
    }
    
    /**
     * Gets the unique reference ID for the OTP request.
     *
     * @return the reference ID, or null if not available
     */
    public String getReferenceId() {
        return referenceId;
    }
    
    /**
     * Gets the suggested action for the user to resolve the issue.
     *
     * @return the suggested action, or null if not available
     */
    public String getSuggestedAction() {
        return suggestedAction;
    }
    
    /**
     * Checks if there are any remaining verification attempts.
     *
     * @return true if there are remaining attempts or if attempts are unlimited, false otherwise
     */
    public boolean hasRemainingAttempts() {
        return remainingAttempts > 0 || remainingAttempts == -1;
    }
}