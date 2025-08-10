package com.tournament_football_backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the tournament management system
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handles authorization denied exceptions
    // This is typically thrown when a user tries to access a resource they are not authorized for
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(
            AuthorizationDeniedException ex, WebRequest request) {

        logger.warn("Authorization denied: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "ACCESS_DENIED",
                "Non hai i permessi per accedere a questa risorsa"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // Handles access denied exceptions
    // This is typically used when a user tries to access a resource they are not authorized for
    // e.g., trying to access a tournament they are not part of
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {

        logger.warn("Access denied: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "ACCESS_DENIED",
                "Non hai i permessi per accedere a questa risorsa"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // Handles authentication exceptions
    // This is typically thrown when a JWT token is invalid or missing
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex, WebRequest request) {

        logger.warn("Authentication failed: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "AUTHENTICATION_FAILED",
                "Token JWT non valido o mancante"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // Handles custom tournament exceptions
    // This is used for specific error codes related to tournament operations
    // e.g., tournament not found, already exists, etc.
    @ExceptionHandler(TournamentException.class)
    public ResponseEntity<ErrorResponse> handleTournamentException(
            TournamentException ex, WebRequest request) {

        logger.warn("Tournament exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        HttpStatus status = determineHttpStatus(ex.getErrorCode());

        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getMessage());

        return new ResponseEntity<>(errorResponse, status);
    }

    // Handles validation exceptions
    // This is typically thrown when input validation fails
    // e.g., required fields are missing or invalid data types
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        logger.warn("Validation failed: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse("VALIDATION_ERROR", "Input validation failed");
        errorResponse.setValidationErrors(errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handles generic exceptions
    // This is a catch-all for any unexpected errors that occur
    // e.g., database errors, null pointer exceptions, etc.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        logger.error("Unexpected error occurred", ex);

        ErrorResponse errorResponse = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Determines the appropriate HTTP status based on error code
     */
    private HttpStatus determineHttpStatus(String errorCode) {
        if (errorCode.contains("NOT_FOUND")) {
            return HttpStatus.NOT_FOUND;
        }
        if (errorCode.contains("ALREADY_EXISTS") ||
                errorCode.contains("ALREADY_IN") ||
                errorCode.contains("FULL") ||
                errorCode.contains("NOT_OPEN")) {
            return HttpStatus.CONFLICT;
        }
        if (errorCode.contains("UNAUTHORIZED")) {
            return HttpStatus.FORBIDDEN;
        }
        if (errorCode.contains("INVALID") ||
                errorCode.contains("VALIDATION") ||
                errorCode.contains("SAME_TEAM")) {
            return HttpStatus.BAD_REQUEST;
        }

        return HttpStatus.BAD_REQUEST; // Default
    }

    /**
     * Standard error response structure
     */
    public static class ErrorResponse {
        private String error;
        private String message;
        private Map<String, String> validationErrors;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        // Getters and setters
        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, String> getValidationErrors() {
            return validationErrors;
        }

        public void setValidationErrors(Map<String, String> validationErrors) {
            this.validationErrors = validationErrors;
        }
    }
}