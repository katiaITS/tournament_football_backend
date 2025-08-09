package com.tournament_football_backend.exception;

/**
 * All team-related exceptions grouped in one file
 */
public class TeamExceptions {

    public static class TeamNotFoundException extends TournamentException {
        public TeamNotFoundException() {
            super("Team not found", "TEAM_NOT_FOUND");
        }
    }

    public static class TeamNameAlreadyExistsException extends TournamentException {
        public TeamNameAlreadyExistsException() {
            super("Team name already exists", "TEAM_NAME_ALREADY_EXISTS");
        }
    }

    public static class PlayerAlreadyInTeamException extends TournamentException {
        public PlayerAlreadyInTeamException() {
            super("Player already in team", "PLAYER_ALREADY_IN_TEAM");
        }
    }

    public static class PlayerNotInTeamException extends TournamentException {
        public PlayerNotInTeamException() {
            super("Player not in team", "PLAYER_NOT_IN_TEAM");
        }
    }
}