package com.zensar.ankit.demo.advice;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.zensar.ankit.demo.exception.OTPAlreadyVerifiedException;
import com.zensar.ankit.demo.exception.OTPAttemptsExceededException;
import com.zensar.ankit.demo.exception.OTPDeliveryFailureException;
import com.zensar.ankit.demo.exception.OTPException;
import com.zensar.ankit.demo.exception.OTPExpiredException;
import com.zensar.ankit.demo.exception.OTPGenerationFailedException;
import com.zensar.ankit.demo.exception.OTPInvalidException;
import com.zensar.ankit.demo.exception.OTPNotFoundException;
import com.zensar.ankit.demo.exception.OTPRateLimitedException;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling for both validation errors and OTP-related exceptions.
 */
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
	
	// Error handle for @Valid
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());

        // Get all errors
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);
    }
    
    /**
     * Common method to build OTP error responses with consistent structure.
     * 
     * @param status HTTP status code
     * @param error Error type
     * @param message Error message
     * @param remainingAttempts Number of remaining attempts (optional)
     * @param retryAfterSeconds Number of seconds to wait before retrying (optional)
     * @param request Web request
     * @return Map containing the error response
     */
    private Map<String, Object> buildOtpErrorResponse(HttpStatus status, String error, String message, 
            Integer remainingAttempts, Integer retryAfterSeconds, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        
        // Generate a request ID if not already present
        String requestId = UUID.randomUUID().toString();
        body.put("requestId", requestId);
        
        // Add remaining attempts if provided
        if (remainingAttempts != null) {
            body.put("remainingAttempts", remainingAttempts);
        }
        
        // Add retry after seconds if provided
        if (retryAfterSeconds != null) {
            body.put("retryAfterSeconds", retryAfterSeconds);
        }
        
        return body;
    }
    
    /**
     * Exception handler for OTPInvalidException.
     * Handles cases where an incorrect OTP is provided during verification.
     * 
     * @param ex The OTPInvalidException
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OTPInvalidException.class)
    public ResponseEntity<Object> handleOTPInvalidException(OTPInvalidException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED; // 401 Unauthorized
        
        Map<String, Object> body = buildOtpErrorResponse(
                status,
                "OTP Verification Failed",
                ex.getMessage(),
                ex.getRemainingAttempts(),
                null,
                request);
        
        return new ResponseEntity<>(body, status);
    }
    
    /**
     * Exception handler for OTPExpiredException.
     * Handles cases where an OTP has exceeded its validity period.
     * 
     * @param ex The OTPExpiredException
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OTPExpiredException.class)
    public ResponseEntity<Object> handleOTPExpiredException(OTPExpiredException ex, WebRequest request) {
        HttpStatus status = HttpStatus.GONE; // 410 Gone
        
        Map<String, Object> body = buildOtpErrorResponse(
                status,
                "OTP Expired",
                ex.getMessage(),
                null,
                null,
                request);
        
        return new ResponseEntity<>(body, status);
    }
    
    /**
     * Exception handler for OTPAttemptsExceededException.
     * Handles cases where the maximum number of verification attempts for an OTP has been exceeded.
     * 
     * @param ex The OTPAttemptsExceededException
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OTPAttemptsExceededException.class)
    public ResponseEntity<Object> handleOTPAttemptsExceededException(OTPAttemptsExceededException ex, WebRequest request) {
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS; // 429 Too Many Requests
        
        Map<String, Object> body = buildOtpErrorResponse(
                status,
                "Maximum Attempts Exceeded",
                ex.getMessage(),
                0, // No remaining attempts
                ex.getRetryAfterSeconds(),
                request);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Retry-After", String.valueOf(ex.getRetryAfterSeconds()));
        
        return new ResponseEntity<>(body, headers, status);
    }
    
    /**
     * Exception handler for OTPNotFoundException.
     * Handles cases where no OTP record is found for a given mobile number or reference ID.
     * 
     * @param ex The OTPNotFoundException
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OTPNotFoundException.class)
    public ResponseEntity<Object> handleOTPNotFoundException(OTPNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404 Not Found
        
        Map<String, Object> body = buildOtpErrorResponse(
                status,
                "OTP Not Found",
                ex.getMessage(),
                null,
                null,
                request);
        
        return new ResponseEntity<>(body, status);
    }
    
    /**
     * Exception handler for OTPRateLimitedException.
     * Handles cases where too many OTP requests are made for a mobile number within a specific time period.
     * 
     * @param ex The OTPRateLimitedException
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OTPRateLimitedException.class)
    public ResponseEntity<Object> handleOTPRateLimitedException(OTPRateLimitedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS; // 429 Too Many Requests
        
        Map<String, Object> body = buildOtpErrorResponse(
                status,
                "Rate Limit Exceeded",
                ex.getMessage(),
                null,
                null,
                request);
        
        return new ResponseEntity<>(body, status);
    }
    
    /**
     * Exception handler for OTPDeliveryFailureException.
     * Handles cases where the system fails to deliver an OTP via SMS.
     * 
     * @param ex The OTPDeliveryFailureException
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OTPDeliveryFailureException.class)
    public ResponseEntity<Object> handleOTPDeliveryFailureException(OTPDeliveryFailureException ex, WebRequest request) {
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE; // 503 Service Unavailable
        
        Map<String, Object> body = buildOtpErrorResponse(
                status,
                "OTP Delivery Failed",
                ex.getMessage(),
                null,
                null,
                request);
        
        return new ResponseEntity<>(body, status);
    }
    
    /**
     * Exception handler for OTPGenerationFailedException.
     * Handles cases where the system fails to generate an OTP due to internal errors.
     * 
     * @param ex The OTPGenerationFailedException
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OTPGenerationFailedException.class)
    public ResponseEntity<Object> handleOTPGenerationFailedException(OTPGenerationFailedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500 Internal Server Error
        
        Map<String, Object> body = buildOtpErrorResponse(
                status,
                "OTP Generation Failed",
                ex.getMessage(),
                null,
                null,
                request);
        
        return new ResponseEntity<>(body, status);
    }
    
    /**
     * Exception handler for OTPAlreadyVerifiedException.
     * Handles cases where an attempt is made to verify an OTP that has already been successfully verified.
     * 
     * @param ex The OTPAlreadyVerifiedException
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OTPAlreadyVerifiedException.class)
    public ResponseEntity<Object> handleOTPAlreadyVerifiedException(OTPAlreadyVerifiedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400 Bad Request
        
        Map<String, Object> body = buildOtpErrorResponse(
                status,
                "OTP Already Verified",
                ex.getMessage(),
                null,
                null,
                request);
        
        return new ResponseEntity<>(body, status);
    }
    
    /**
     * Generic exception handler for OTPException.
     * Handles any OTP-related exceptions not covered by more specific handlers.
     * 
     * @param ex The OTPException
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OTPException.class)
    public ResponseEntity<Object> handleOTPException(OTPException ex, WebRequest request) {
        HttpStatus status = ex.getHttpStatus();
        
        Map<String, Object> body = buildOtpErrorResponse(
                status,
                "OTP Error",
                ex.getMessage(),
                null,
                null,
                request);
        
        return new ResponseEntity<>(body, status);
    }
}