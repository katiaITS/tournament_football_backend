package com.tournament_football_backend.exception;


/**
 * All tournament-related exceptions grouped in one file
 */
public class TournamentExceptions {

    public static class TournamentNotFoundException extends TournamentException {
        public TournamentNotFoundException() {
            super("Tournament not found", "TOURNAMENT_NOT_FOUND");
        }
    }

    public static class InvalidTournamentDateException extends TournamentException {
        public InvalidTournamentDateException() {
            super("Start date must be before end date", "INVALID_TOURNAMENT_DATE");
        }
    }

    public static class TournamentNotOpenException extends TournamentException {
        public TournamentNotOpenException() {
            super("Tournament is not open for registration", "TOURNAMENT_NOT_OPEN");
        }
    }

    public static class TournamentFullException extends TournamentException {
        public TournamentFullException() {
            super("Tournament has reached maximum number of teams", "TOURNAMENT_FULL");
        }
    }

    public static class TeamAlreadyInTournamentException extends TournamentException {
        public TeamAlreadyInTournamentException() {
            super("Team is already registered in tournament", "TEAM_ALREADY_IN_TOURNAMENT");
        }
    }

    public static class TeamNotInTournamentException extends TournamentException {
        public TeamNotInTournamentException() {
            super("Team is not registered in tournament", "TEAM_NOT_IN_TOURNAMENT");
        }
    }
}