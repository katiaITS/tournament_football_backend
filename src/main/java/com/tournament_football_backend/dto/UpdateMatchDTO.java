package com.tournament_football_backend.dto;

import jakarta.validation.constraints.Min;
import com.tournament_football_backend.model.MatchStatus;

import java.time.LocalDateTime;

public class UpdateMatchDTO {

    // All fields optional for partial updates
    private LocalDateTime matchDate;

    @Min(0)
    private Integer homeGoals;

    @Min(0)
    private Integer awayGoals;

    private MatchStatus status;

    // Constructors
    public UpdateMatchDTO() {}

    // Getters and Setters
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