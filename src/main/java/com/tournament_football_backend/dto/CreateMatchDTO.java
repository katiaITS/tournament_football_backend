package com.tournament_football_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.tournament_football_backend.model.MatchStatus;

import java.time.LocalDateTime;

public class CreateMatchDTO {

    @NotNull
    private Long homeTeamId;

    @NotNull
    private Long awayTeamId;

    @NotNull
    private Long tournamentId;

    private LocalDateTime matchDate;

    @Min(0)
    private Integer homeGoals = 0;

    @Min(0)
    private Integer awayGoals = 0;

    private MatchStatus status = MatchStatus.SCHEDULED;

    // Constructors
    public CreateMatchDTO() {}

    public CreateMatchDTO(Long homeTeamId, Long awayTeamId, Long tournamentId) {
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.tournamentId = tournamentId;
    }

    // Getters and Setters
    public Long getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(Long homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public Long getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(Long awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public Integer getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(Integer homeGoals) {
        this.homeGoals = homeGoals;
    }

    public Integer getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(Integer awayGoals) {
        this.awayGoals = awayGoals;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }
}