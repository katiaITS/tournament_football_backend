package com.tournament_football_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.tournament_football_backend.model.TournamentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/* * Tournament Data Transfer Object
 * Represents a tournament with its details and participating teams.
 */
public class TournamentDTO {

    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private Integer maxTeams = 16;
    private TournamentStatus status = TournamentStatus.OPEN;
    private LocalDateTime createdAt;
    private Set<TeamDTO> participatingTeams;
    private int numberOfRegisteredTeams;

    // Constructors
    public TournamentDTO() {}

    public TournamentDTO(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<TeamDTO> getParticipatingTeams() {
        return participatingTeams;
    }

    public void setParticipatingTeams(Set<TeamDTO> participatingTeams) {
        this.participatingTeams = participatingTeams;
    }

    public int getNumberOfRegisteredTeams() {
        return numberOfRegisteredTeams;
    }

    public void setNumberOfRegisteredTeams(int numberOfRegisteredTeams) {
        this.numberOfRegisteredTeams = numberOfRegisteredTeams;
    }
}