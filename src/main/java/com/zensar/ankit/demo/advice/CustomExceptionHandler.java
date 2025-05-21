package com.zensar.ankit.demo.advice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler for validation errors in the application.
 * Extends Spring's ResponseEntityExceptionHandler to provide custom error responses.
 */
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
	
	/**
	 * Record for structured error response
	 * Provides a clean, immutable representation of validation errors
	 */
	public record ValidationErrorResponse(Instant timestamp, int status, List<String> errors) {}
	
	/**
	 * Handles validation errors for @Valid annotated request bodies
	 * Overrides the default implementation to provide a more detailed error response
	 */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status, 
                                                                  WebRequest request) {

        // Create response body with enhanced context
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("path", request.getDescription(false).substring(4));

        // Extract all validation errors using pattern matching and enhanced stream operations
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> 
                    "%s: %s".formatted(fieldError.getField(), fieldError.getDefaultMessage())
                )
                .toList();

        // Add global errors if any
        List<String> globalErrors = ex.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(objectError -> 
                    "%s: %s".formatted(objectError.getObjectName(), objectError.getDefaultMessage())
                )
                .toList();
        
        if (!globalErrors.isEmpty()) {
            errors = Stream.concat(errors.stream(), globalErrors.stream()).toList();
        }

        body.put("errors", errors);
        body.put("errorCount", errors.size());

        return new ResponseEntity<>(body, headers, status);
    }
}