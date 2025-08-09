package com.tournament_football_backend.exception;

/**
 * All validation-related exceptions grouped in one file
 */
public class ValidationExceptions {

    public static class ValidationException extends TournamentException {
        public ValidationException(String message) {
            super(message, "VALIDATION_ERROR");
        }
    }

    public static class EmptySearchKeywordException extends TournamentException {
        public EmptySearchKeywordException() {
            super("Search keyword cannot be empty", "EMPTY_SEARCH_KEYWORD");
        }
    }

    public static class InvalidParameterException extends TournamentException {
        public InvalidParameterException(String parameter) {
            super("Invalid parameter: " + parameter, "INVALID_PARAMETER");
        }
    }
}