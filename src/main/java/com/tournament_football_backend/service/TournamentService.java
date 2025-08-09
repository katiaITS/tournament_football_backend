package com.tournament_football_backend.service;

import com.tournament_football_backend.dto.CreateTournamentDTO;
import com.tournament_football_backend.dto.TeamDTO;
import com.tournament_football_backend.dto.TournamentDTO;
import com.tournament_football_backend.dto.UpdateTournamentDTO;
import com.tournament_football_backend.model.Team;
import com.tournament_football_backend.model.Tournament;
import com.tournament_football_backend.model.TournamentStatus;
import com.tournament_football_backend.repository.TeamRepository;
import com.tournament_football_backend.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tournament_football_backend.exception.TeamExceptions.*;
import static com.tournament_football_backend.exception.TournamentExceptions.*;
import static com.tournament_football_backend.exception.ValidationExceptions.*;

@Service
@Transactional
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    public List<TournamentDTO> getAllTournaments() {
        return tournamentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<TournamentDTO> getTournamentById(Long id) {
        return tournamentRepository.findByIdWithTeams(id)
                .map(this::convertToDTO);
    }

    public TournamentDTO createTournament(CreateTournamentDTO createTournamentDTO) {
        if (createTournamentDTO.getStartDate().isAfter(createTournamentDTO.getEndDate())) {
            throw new InvalidTournamentDateException();
        }

        Tournament tournament = new Tournament();
        tournament.setName(createTournamentDTO.getName());
        tournament.setDescription(createTournamentDTO.getDescription());
        tournament.setStartDate(createTournamentDTO.getStartDate());
        tournament.setEndDate(createTournamentDTO.getEndDate());
        tournament.setMaxTeams(createTournamentDTO.getMaxTeams());
        tournament.setStatus(createTournamentDTO.getStatus() != null ? createTournamentDTO.getStatus() : TournamentStatus.OPEN);

        Tournament savedTournament = tournamentRepository.save(tournament);
        return convertToDTO(savedTournament);
    }

    public Optional<TournamentDTO> updateTournament(Long id, UpdateTournamentDTO updateTournamentDTO) {
        return tournamentRepository.findById(id)
                .map(tournament -> {
                    if (updateTournamentDTO.getName() != null)
                        tournament.setName(updateTournamentDTO.getName());
                    if (updateTournamentDTO.getDescription() != null)
                        tournament.setDescription(updateTournamentDTO.getDescription());
                    if (updateTournamentDTO.getStartDate() != null)
                        tournament.setStartDate(updateTournamentDTO.getStartDate());
                    if (updateTournamentDTO.getEndDate() != null)
                        tournament.setEndDate(updateTournamentDTO.getEndDate());
                    if (updateTournamentDTO.getMaxTeams() != null)
                        tournament.setMaxTeams(updateTournamentDTO.getMaxTeams());
                    if (updateTournamentDTO.getStatus() != null)
                        tournament.setStatus(updateTournamentDTO.getStatus());

                    // Validation only if both dates are present
                    if (tournament.getStartDate() != null && tournament.getEndDate() != null &&
                            tournament.getStartDate().isAfter(tournament.getEndDate())) {
                        throw new InvalidTournamentDateException();
                    }

                    return convertToDTO(tournamentRepository.save(tournament));
                });
    }

    public boolean deleteTournament(Long id) {
        if (!tournamentRepository.existsById(id)) {
            throw new TournamentNotFoundException();
        }

        tournamentRepository.deleteById(id);
        return true;
    }

    public boolean registerTeam(Long tournamentId, Long teamId) {
        Tournament tournament = tournamentRepository.findByIdWithTeams(tournamentId)
                .orElseThrow(TournamentNotFoundException::new);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);

        if (tournament.getStatus() != TournamentStatus.OPEN) {
            throw new TournamentNotOpenException();
        }

        if (tournament.isFull()) {
            throw new TournamentFullException();
        }

        if (tournament.getParticipatingTeams().contains(team)) {
            throw new TeamAlreadyInTournamentException();
        }

        tournament.addTeam(team);
        tournamentRepository.save(tournament);
        return true;
    }

    public boolean removeTeam(Long tournamentId, Long teamId) {
        Tournament tournament = tournamentRepository.findByIdWithTeams(tournamentId)
                .orElseThrow(TournamentNotFoundException::new);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);

        if (!tournament.getParticipatingTeams().contains(team)) {
            throw new TeamNotInTournamentException();
        }

        tournament.removeTeam(team);
        tournamentRepository.save(tournament);
        return true;
    }

    public List<TournamentDTO> getTournamentsByStatus(TournamentStatus status) {
        return tournamentRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TournamentDTO> getUpcomingTournaments() {
        return tournamentRepository.findByStartDateAfter(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TournamentDTO> getTournamentsByTeam(Long teamId) {
        // Verify team exists
        if (!teamRepository.existsById(teamId)) {
            throw new TeamNotFoundException();
        }

        return tournamentRepository.findByParticipatingTeamId(teamId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TournamentDTO> searchTournaments(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new EmptySearchKeywordException();
        }

        return tournamentRepository.findByNameContaining(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TournamentDTO convertToDTO(Tournament tournament) {
        TournamentDTO dto = new TournamentDTO();
        dto.setId(tournament.getId());
        dto.setName(tournament.getName());
        dto.setDescription(tournament.getDescription());
        dto.setStartDate(tournament.getStartDate());
        dto.setEndDate(tournament.getEndDate());
        dto.setMaxTeams(tournament.getMaxTeams());
        dto.setStatus(tournament.getStatus());
        dto.setCreatedAt(tournament.getCreatedAt());
        dto.setNumberOfRegisteredTeams(tournament.getParticipatingTeams().size());

        if (tournament.getParticipatingTeams() != null && !tournament.getParticipatingTeams().isEmpty()) {
            Set<TeamDTO> teamsDTO = tournament.getParticipatingTeams().stream()
                    .map(this::convertTeamToDTO)
                    .collect(Collectors.toSet());
            dto.setParticipatingTeams(teamsDTO);
        }

        return dto;
    }

    private TeamDTO convertTeamToDTO(Team team) {
        TeamDTO dto = new TeamDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setNumberOfPlayers(team.getPlayers().size());
        return dto;
    }
}