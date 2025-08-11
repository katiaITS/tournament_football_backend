package com.tournament_football_backend.exception;

/**
 * Base exception for all business exceptions in the tournament management system.
 * Provides a consistent error handling structure with error codes for proper
 * client-side error identification and logging purposes.
 */
public abstract class TournamentException extends RuntimeException {
    private final String errorCode;

    // Error codes are used to identify specific exceptions for consistent API responses
    protected TournamentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    // Constructor with cause for exceptions that wrap other exceptions
    protected TournamentException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    // Getter for the error code
    public String getErrorCode() {
        return errorCode;
    }
}