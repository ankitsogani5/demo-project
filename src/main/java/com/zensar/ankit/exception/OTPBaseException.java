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
     * Number of remaining verification attempts allowed for this OTP
     */
    private final Integer remainingAttempts;
    
    /**
     * Reference ID for correlating OTP requests
     */
    private final String referenceId;
    
    /**
     * Suggested action for the user to resolve the issue
     */
    private final String suggestedAction;

    /**
     * Constructs a new OTPBaseException with the specified error code, message, HTTP status, 
     * remaining attempts, reference ID, and suggested action.
     *
     * @param errorCode The error code associated with this exception
     * @param message The detailed error message
     * @param httpStatus The HTTP status code to be returned to the client
     * @param remainingAttempts Number of remaining verification attempts allowed
     * @param referenceId Reference ID for correlating OTP requests
     * @param suggestedAction Suggested action for the user to resolve the issue
     */
    public OTPBaseException(String errorCode, String message, HttpStatus httpStatus, 
                           Integer remainingAttempts, String referenceId, String suggestedAction) {
        super(errorCode, message, httpStatus);
        this.remainingAttempts = remainingAttempts;
        this.referenceId = referenceId;
        this.suggestedAction = suggestedAction;
    }

    /**
     * Constructs a new OTPBaseException with the specified error code, message, HTTP status, 
     * and cause, along with remaining attempts, reference ID, and suggested action.
     *
     * @param errorCode The error code associated with this exception
     * @param message The detailed error message
     * @param httpStatus The HTTP status code to be returned to the client
     * @param cause The cause of this exception
     * @param remainingAttempts Number of remaining verification attempts allowed
     * @param referenceId Reference ID for correlating OTP requests
     * @param suggestedAction Suggested action for the user to resolve the issue
     */
    public OTPBaseException(String errorCode, String message, HttpStatus httpStatus, Throwable cause,
                           Integer remainingAttempts, String referenceId, String suggestedAction) {
        super(errorCode, message, httpStatus, cause);
        this.remainingAttempts = remainingAttempts;
        this.referenceId = referenceId;
        this.suggestedAction = suggestedAction;
    }

    /**
     * Constructs a new OTPBaseException with the specified error code, message, and HTTP status,
     * along with remaining attempts and reference ID, but no suggested action.
     *
     * @param errorCode The error code associated with this exception
     * @param message The detailed error message
     * @param httpStatus The HTTP status code to be returned to the client
     * @param remainingAttempts Number of remaining verification attempts allowed
     * @param referenceId Reference ID for correlating OTP requests
     */
    public OTPBaseException(String errorCode, String message, HttpStatus httpStatus, 
                           Integer remainingAttempts, String referenceId) {
        this(errorCode, message, httpStatus, remainingAttempts, referenceId, null);
    }

    /**
     * Constructs a new OTPBaseException with the specified error code, message, and HTTP status,
     * along with reference ID, but no remaining attempts or suggested action.
     *
     * @param errorCode The error code associated with this exception
     * @param message The detailed error message
     * @param httpStatus The HTTP status code to be returned to the client
     * @param referenceId Reference ID for correlating OTP requests
     */
    public OTPBaseException(String errorCode, String message, HttpStatus httpStatus, String referenceId) {
        this(errorCode, message, httpStatus, null, referenceId, null);
    }

    /**
     * Constructs a new OTPBaseException with the specified error code, message, and HTTP status,
     * but no remaining attempts, reference ID, or suggested action.
     *
     * @param errorCode The error code associated with this exception
     * @param message The detailed error message
     * @param httpStatus The HTTP status code to be returned to the client
     */
    public OTPBaseException(String errorCode, String message, HttpStatus httpStatus) {
        this(errorCode, message, httpStatus, null, null, null);
    }

    /**
     * Returns the number of remaining verification attempts allowed for this OTP.
     *
     * @return The number of remaining attempts, or null if not applicable
     */
    public Integer getRemainingAttempts() {
        return remainingAttempts;
    }

    /**
     * Returns the reference ID for correlating OTP requests.
     *
     * @return The reference ID, or null if not applicable
     */
    public String getReferenceId() {
        return referenceId;
    }

    /**
     * Returns the suggested action for the user to resolve the issue.
     *
     * @return The suggested action, or null if not applicable
     */
    public String getSuggestedAction() {
        return suggestedAction;
    }
}