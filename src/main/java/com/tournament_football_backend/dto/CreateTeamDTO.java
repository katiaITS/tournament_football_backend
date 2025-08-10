package com.tournament_football_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/* * CreateTeamDTO is used for creating a new team.
 * It contains the necessary fields and validation constraints.
 */
public class CreateTeamDTO {

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    // Constructors
    public CreateTeamDTO() {}

    public CreateTeamDTO(String name) {
        this.name = name;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}