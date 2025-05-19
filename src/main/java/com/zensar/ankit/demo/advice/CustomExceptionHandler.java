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

import com.zensar.ankit.demo.exception.OTPAttemptsExceededException;
import com.zensar.ankit.demo.exception.OTPException;
import com.zensar.ankit.demo.exception.OTPExpiredException;
import com.zensar.ankit.demo.exception.OTPInvalidException;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
	
	 // error handle for @Valid
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus  status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());

        //Get all errors
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);
    }
    
    /**
     * Handles OTPInvalidException which occurs when an incorrect OTP is provided during verification.
     * Returns a 401 Unauthorized status with details about remaining attempts.
     * 
     * @param ex the OTPInvalidException
     * @param request the current request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OTPInvalidException.class)
    public ResponseEntity<Object> handleOTPInvalidException(OTPInvalidException ex, WebRequest request) {
        return buildOTPErrorResponse(
                ex.getMessage(),
                "OTP Verification Failed",
                HttpStatus.UNAUTHORIZED,
                ex.getRemainingAttempts(),
                generateRequestId(request)
        );
    }
    
    /**
     * Handles OTPExpiredException which occurs when an OTP has exceeded its time-to-live period.
     * Returns a 410 Gone status indicating the resource is no longer available.
     * 
     * @param ex the OTPExpiredException
     * @param request the current request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OTPExpiredException.class)
    public ResponseEntity<Object> handleOTPExpiredException(OTPExpiredException ex, WebRequest request) {
        return buildOTPErrorResponse(
                ex.getMessage(),
                "OTP Expired",
                HttpStatus.GONE,
                null,
                generateRequestId(request)
        );
    }
    
    /**
     * Handles OTPAttemptsExceededException which occurs when the maximum number of verification attempts has been exceeded.
     * Returns a 429 Too Many Requests status with a Retry-After header.
     * 
     * @param ex the OTPAttemptsExceededException
     * @param request the current request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OTPAttemptsExceededException.class)
    public ResponseEntity<Object> handleOTPAttemptsExceededException(OTPAttemptsExceededException ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Retry-After", String.valueOf(ex.getRetryAfterSeconds()));
        
        ResponseEntity<Object> response = buildOTPErrorResponse(
                ex.getMessage(),
                "Maximum Attempts Exceeded",
                HttpStatus.TOO_MANY_REQUESTS,
                0, // No remaining attempts
                generateRequestId(request)
        );
        
        return new ResponseEntity<>(response.getBody(), headers, response.getStatusCode());
    }
    
    /**
     * Handles all other OTP-related exceptions.
     * Returns the appropriate status code based on the exception.
     * 
     * @param ex the OTPException
     * @param request the current request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OTPException.class)
    public ResponseEntity<Object> handleOTPException(OTPException ex, WebRequest request) {
        return buildOTPErrorResponse(
                ex.getMessage(),
                ex.getErrorCode(),
                ex.getStatus(),
                ex.getDetails() instanceof Integer ? (Integer) ex.getDetails() : null,
                generateRequestId(request)
        );
    }
    
    /**
     * Builds a consistent error response for OTP-related exceptions.
     * 
     * @param message the error message
     * @param error the error type
     * @param status the HTTP status code
     * @param remainingAttempts the number of remaining attempts, or null if not applicable
     * @param requestId the unique request identifier
     * @return ResponseEntity with formatted error details
     */
    private ResponseEntity<Object> buildOTPErrorResponse(String message, String error, HttpStatus status, 
                                                        Integer remainingAttempts, String requestId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("requestId", requestId);
        
        if (remainingAttempts != null) {
            body.put("remainingAttempts", remainingAttempts);
        }
        
        return new ResponseEntity<>(body, status);
    }
    
    /**
     * Generates a unique request ID for tracking purposes.
     * 
     * @param request the current request
     * @return a unique request ID string
     */
    private String generateRequestId(WebRequest request) {
        // Try to get existing request ID from request attributes
        String requestId = (String) request.getAttribute("requestId", WebRequest.SCOPE_REQUEST);
        
        // If no request ID exists, generate a new one
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
            request.setAttribute("requestId", requestId, WebRequest.SCOPE_REQUEST);
        }
        
        return requestId;
    }
}