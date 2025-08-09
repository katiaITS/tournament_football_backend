package com.tournament_football_backend.dto;

import jakarta.validation.constraints.Size;
import com.tournament_football_backend.model.TournamentStatus;

import java.time.LocalDate;

public class UpdateTournamentDTO {

    // All fields optional for partial updates
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 1000)
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer maxTeams;
    private TournamentStatus status;

    // Constructors
    public UpdateTournamentDTO() {}

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getMaxTeams() {
        return maxTeams;
    }

    public void setMaxTeams(Integer maxTeams) {
        this.maxTeams = maxTeams;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }
}