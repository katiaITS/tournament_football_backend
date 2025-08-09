package com.tournament_football_backend.exception;

/**
 * All user-related exceptions grouped in one file
 */
public class UserExceptions {

    public static class UserNotFoundException extends TournamentException {
        public UserNotFoundException() {
            super("User not found", "USER_NOT_FOUND");
        }
    }

    public static class UsernameAlreadyExistsException extends TournamentException {
        public UsernameAlreadyExistsException() {
            super("Username already exists", "USERNAME_ALREADY_EXISTS");
        }
    }

    public static class EmailAlreadyExistsException extends TournamentException {
        public EmailAlreadyExistsException() {
            super("Email already exists", "EMAIL_ALREADY_EXISTS");
        }
    }

    public static class UnauthorizedOperationException extends TournamentException {
        public UnauthorizedOperationException() {
            super("Unauthorized operation", "UNAUTHORIZED_OPERATION");
        }
    }
}