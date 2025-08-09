package com.tournament_football_backend.controller;

import com.tournament_football_backend.dto.CreateTournamentDTO;
import com.tournament_football_backend.dto.TournamentDTO;
import com.tournament_football_backend.dto.UpdateTournamentDTO;
import com.tournament_football_backend.model.TournamentStatus;
import com.tournament_football_backend.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    // GET /api/tournaments - List all tournaments
    @GetMapping
    public ResponseEntity<List<TournamentDTO>> getAllTournaments() {
        List<TournamentDTO> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }

    // GET /api/tournaments/{id} - Get tournament by ID
    @GetMapping("/{id}")
    public ResponseEntity<TournamentDTO> getTournamentById(@PathVariable Long id) {
        return tournamentService.getTournamentById(id)
                .map(tournament -> ResponseEntity.ok(tournament))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/tournaments - Create new tournament (ADMIN only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentDTO> createTournament(@Valid @RequestBody CreateTournamentDTO createTournamentDTO) {
        TournamentDTO createdTournament = tournamentService.createTournament(createTournamentDTO);
        return ResponseEntity.ok(createdTournament);
    }

    // PUT /api/tournaments/{id} - Update tournament (ADMIN only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentDTO> updateTournament(@PathVariable Long id, @Valid @RequestBody UpdateTournamentDTO updateTournamentDTO) {
        return tournamentService.updateTournament(id, updateTournamentDTO)
                .map(updatedTournament -> ResponseEntity.ok(updatedTournament))
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/tournaments/{id} - Delete tournament (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.ok().build();
    }

    // POST /api/tournaments/{tournamentId}/teams/{teamId} - Register team to tournament
    @PostMapping("/{tournamentId}/teams/{teamId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> registerTeam(@PathVariable Long tournamentId, @PathVariable Long teamId) {
        tournamentService.registerTeam(tournamentId, teamId);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/tournaments/{tournamentId}/teams/{teamId} - Remove team from tournament (ADMIN only)
    @DeleteMapping("/{tournamentId}/teams/{teamId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeTeam(@PathVariable Long tournamentId, @PathVariable Long teamId) {
        tournamentService.removeTeam(tournamentId, teamId);
        return ResponseEntity.ok().build();
    }

    // GET /api/tournaments/status/{status} - Tournaments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TournamentDTO>> getTournamentsByStatus(@PathVariable TournamentStatus status) {
        List<TournamentDTO> tournaments = tournamentService.getTournamentsByStatus(status);
        return ResponseEntity.ok(tournaments);
    }

    // GET /api/tournaments/upcoming - Upcoming tournaments
    @GetMapping("/upcoming")
    public ResponseEntity<List<TournamentDTO>> getUpcomingTournaments() {
        List<TournamentDTO> tournaments = tournamentService.getUpcomingTournaments();
        return ResponseEntity.ok(tournaments);
    }

    // GET /api/tournaments/team/{teamId} - Tournaments by team
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TournamentDTO>> getTournamentsByTeam(@PathVariable Long teamId) {
        List<TournamentDTO> tournaments = tournamentService.getTournamentsByTeam(teamId);
        return ResponseEntity.ok(tournaments);
    }

    // GET /api/tournaments/search?keyword={keyword} - Search tournaments
    @GetMapping("/search")
    public ResponseEntity<List<TournamentDTO>> searchTournaments(@RequestParam String keyword) {
        List<TournamentDTO> tournaments = tournamentService.searchTournaments(keyword);
        return ResponseEntity.ok(tournaments);
    }
}