package com.tournament_football_backend.exception;

/**
 * All match-related exceptions grouped in one file
 */
public class MatchExceptions {

    public static class MatchNotFoundException extends TournamentException {
        public MatchNotFoundException() {
            super("Match not found", "MATCH_NOT_FOUND");
        }
    }

    public static class SameTeamMatchException extends TournamentException {
        public SameTeamMatchException() {
            super("A team cannot play against itself", "SAME_TEAM_MATCH");
        }
    }

    public static class TeamsNotInTournamentException extends TournamentException {
        public TeamsNotInTournamentException() {
            super("Both teams must be registered in the tournament", "TEAMS_NOT_IN_TOURNAMENT");
        }
    }

    public static class InvalidMatchResultException extends TournamentException {
        public InvalidMatchResultException() {
            super("Invalid match result", "INVALID_MATCH_RESULT");
        }
    }
}