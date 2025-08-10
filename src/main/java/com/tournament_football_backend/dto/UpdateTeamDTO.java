package com.tournament_football_backend.dto;

import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating a team.
 * All fields are optional to allow partial updates.
 */
public class UpdateTeamDTO {

    // All fields optional for partial updates
    @Size(min = 2, max = 50)
    private String name;

    // Constructors
    public UpdateTeamDTO() {}

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}