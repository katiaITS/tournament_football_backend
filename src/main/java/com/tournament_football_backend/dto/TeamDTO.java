package com.tournament_football_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object for Team entity.
 * Contains basic team information and a set of players.
 */
public class TeamDTO {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    private LocalDateTime createdAt;
    private Set<UserDTO> players;
    private int numberOfPlayers;

    // Constructors
    public TeamDTO() {}

    public TeamDTO(String name) {
        this.name = name;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<UserDTO> getPlayers() {
        return players;
    }

    public void setPlayers(Set<UserDTO> players) {
        this.players = players;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }
}