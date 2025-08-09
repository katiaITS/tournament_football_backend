package com.tournament_football_backend.exception;

/**
 * Base exception for all business exceptions in the tournament management system
 */
public abstract class TournamentException extends RuntimeException {
    private final String errorCode;

    protected TournamentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected TournamentException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}