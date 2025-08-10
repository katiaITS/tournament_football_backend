package com.tournament_football_backend.controller;

import com.tournament_football_backend.dto.CreateTeamDTO;
import com.tournament_football_backend.dto.TeamDTO;
import com.tournament_football_backend.dto.UpdateTeamDTO;
import com.tournament_football_backend.exception.TeamExceptions;
import com.tournament_football_backend.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    * TeamController.java
    * Handles team-related API endpoints.
    * Provides functionality to manage teams, including creating, updating, deleting,
    * and retrieving teams and their players.
    *
    * Endpoints:
    * - GET /api/teams - List all teams
    * - GET /api/teams/{id} - Get team by ID
    * - GET /api/teams/name/{name} - Get team by name
    * - POST /api/teams - Create new team
    * - PUT /api/teams/{id} - Update team (ADMIN only)
    * - DELETE /api/teams/{id} - Delete team (ADMIN only)
    * - POST /api/teams/{teamId}/players/{playerId} - Add player to team (ADMIN only)
    * - DELETE /api/teams/{teamId}/players/{playerId} - Remove player from team (ADMIN only)
    * - GET /api/teams/player/{playerId} - Get teams by player ID
    * - GET /api/teams/search?keyword={keyword} - Search teams by keyword
    *
 */
@RestController
@RequestMapping("/teams")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TeamController {

    @Autowired
    private TeamService teamService;

    // GET /api/teams - List all teams
    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        List<TeamDTO> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    // GET /api/teams/{id} - Get team by ID
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        TeamDTO team = teamService.getTeamById(id)
                .orElseThrow(() -> new TeamExceptions.TeamNotFoundException());
        return ResponseEntity.ok(team);
    }

    // GET /api/teams/name/{name} - Get team by name
    @GetMapping("/name/{name}")
    public ResponseEntity<TeamDTO> getTeamByName(@PathVariable String name) {
        return teamService.getTeamByName(name)
                .map(team -> ResponseEntity.ok(team))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/teams - Create new team
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody CreateTeamDTO createTeamDTO) {
        TeamDTO createdTeam = teamService.createTeam(createTeamDTO);
        return ResponseEntity.ok(createdTeam);
    }

    // PUT /api/teams/{id} - Update team (ADMIN only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamDTO> updateTeam(@PathVariable Long id, @Valid @RequestBody UpdateTeamDTO updateTeamDTO) {
        return teamService.updateTeam(id, updateTeamDTO)
                .map(updatedTeam -> ResponseEntity.ok(updatedTeam))
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/teams/{id} - Delete team (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok().build();
    }

    // POST /api/teams/{teamId}/players/{playerId} - Add player
    @PostMapping("/{teamId}/players/{playerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addPlayer(@PathVariable Long teamId, @PathVariable Long playerId) {
        teamService.addPlayer(teamId, playerId);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/teams/{teamId}/players/{playerId} - Remove player
    @DeleteMapping("/{teamId}/players/{playerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removePlayer(@PathVariable Long teamId, @PathVariable Long playerId) {
        teamService.removePlayer(teamId, playerId);
        return ResponseEntity.ok().build();
    }

    // GET /api/teams/player/{playerId} - Teams by player
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<TeamDTO>> getTeamsByPlayer(@PathVariable Long playerId) {
        List<TeamDTO> teams = teamService.getTeamsByPlayer(playerId);
        return ResponseEntity.ok(teams);
    }

    // GET /api/teams/search?keyword={keyword} - Search teams
    @GetMapping("/search")
    public ResponseEntity<List<TeamDTO>> searchTeams(@RequestParam String keyword) {
        List<TeamDTO> teams = teamService.searchTeams(keyword);
        return ResponseEntity.ok(teams);
    }
}