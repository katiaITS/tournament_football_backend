package com.tournament_football_backend.controller;

import com.tournament_football_backend.dto.CreateMatchDTO;
import com.tournament_football_backend.dto.MatchDTO;
import com.tournament_football_backend.dto.UpdateMatchDTO;
import com.tournament_football_backend.model.MatchStatus;
import com.tournament_football_backend.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/matches")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MatchController {

    @Autowired
    private MatchService matchService;

    // GET /api/matches - List all matches
    @GetMapping
    public ResponseEntity<List<MatchDTO>> getAllMatches() {
        List<MatchDTO> matches = matchService.getAllMatches();
        return ResponseEntity.ok(matches);
    }

    // GET /api/matches/{id} - Get match by ID
    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getMatchById(@PathVariable Long id) {
        return matchService.getMatchById(id)
                .map(match -> ResponseEntity.ok(match))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/matches - Create new match (ADMIN only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatchDTO> createMatch(@Valid @RequestBody CreateMatchDTO createMatchDTO) {
        MatchDTO createdMatch = matchService.createMatch(createMatchDTO);
        return ResponseEntity.ok(createdMatch);
    }

    // PUT /api/matches/{id} - Update match (ADMIN only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatchDTO> updateMatch(@PathVariable Long id, @Valid @RequestBody UpdateMatchDTO updateMatchDTO) {
        return matchService.updateMatch(id, updateMatchDTO)
                .map(updatedMatch -> ResponseEntity.ok(updatedMatch))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/matches/{id}/result - Update match result (ADMIN only)
    @PutMapping("/{id}/result")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatchDTO> updateResult(@PathVariable Long id, @RequestBody Map<String, Integer> result) {
        Integer homeGoals = result.get("homeGoals");
        Integer awayGoals = result.get("awayGoals");

        return matchService.updateResult(id, homeGoals, awayGoals)
                .map(updatedMatch -> ResponseEntity.ok(updatedMatch))
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/matches/{id} - Delete match (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMatch(@PathVariable Long id) {
        matchService.deleteMatch(id);
        return ResponseEntity.ok().build();
    }

    // GET /api/matches/tournament/{tournamentId} - Matches by tournament
    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<MatchDTO>> getMatchesByTournament(@PathVariable Long tournamentId) {
        List<MatchDTO> matches = matchService.getMatchesByTournament(tournamentId);
        return ResponseEntity.ok(matches);
    }

    // GET /api/matches/team/{teamId} - Matches by team
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<MatchDTO>> getMatchesByTeam(@PathVariable Long teamId) {
        List<MatchDTO> matches = matchService.getMatchesByTeam(teamId);
        return ResponseEntity.ok(matches);
    }

    // GET /api/matches/status/{status} - Matches by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MatchDTO>> getMatchesByStatus(@PathVariable MatchStatus status) {
        List<MatchDTO> matches = matchService.getMatchesByStatus(status);
        return ResponseEntity.ok(matches);
    }

    // GET /api/matches/period?start={start}&end={end} - Matches by period
    @GetMapping("/period")
    public ResponseEntity<List<MatchDTO>> getMatchesByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<MatchDTO> matches = matchService.getMatchesByPeriod(start, end);
        return ResponseEntity.ok(matches);
    }

    // GET /api/matches/today - Today's matches
    @GetMapping("/today")
    public ResponseEntity<List<MatchDTO>> getTodayMatches() {
        List<MatchDTO> matches = matchService.getTodayMatches();
        return ResponseEntity.ok(matches);
    }
}