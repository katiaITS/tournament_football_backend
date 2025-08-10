package com.tournament_football_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/* * Team entity representing a football team in the tournament system.
 * It contains fields for team name, creation date, and relationships with players and tournaments.
 */
@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> players = new HashSet<>();

    @ManyToMany(mappedBy = "participatingTeams")
    @JsonIgnore
    private Set<Tournament> tournaments = new HashSet<>();

    public Team() {
        this.createdAt = LocalDateTime.now();
    }

    public void addPlayer(User player) {
        this.players.add(player);
        player.getTeams().add(this);
    }

    public void removePlayer(User player) {
        this.players.remove(player);
        player.getTeams().remove(this);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<User> getPlayers() { return players; }
    public void setPlayers(Set<User> players) { this.players = players; }

    public Set<Tournament> getTournaments() { return tournaments; }
    public void setTournaments(Set<Tournament> tournaments) { this.tournaments = tournaments; }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name +
                '}';
    }
}