package com.tournament_football_backend.service;

import com.tournament_football_backend.dto.CreateMatchDTO;
import com.tournament_football_backend.dto.MatchDTO;
import com.tournament_football_backend.dto.UpdateMatchDTO;
import com.tournament_football_backend.model.Match;
import com.tournament_football_backend.model.Team;
import com.tournament_football_backend.model.MatchStatus;
import com.tournament_football_backend.model.Tournament;
import com.tournament_football_backend.repository.MatchRepository;
import com.tournament_football_backend.repository.TeamRepository;
import com.tournament_football_backend.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tournament_football_backend.exception.MatchExceptions.*;
import static com.tournament_football_backend.exception.TeamExceptions.*;
import static com.tournament_football_backend.exception.TournamentExceptions.*;
import static com.tournament_football_backend.exception.ValidationExceptions.*;

@Service
@Transactional
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    public List<MatchDTO> getAllMatches() {
        return matchRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<MatchDTO> getMatchById(Long id) {
        return matchRepository.findById(id)
                .map(this::convertToDTO);
    }

    public MatchDTO createMatch(CreateMatchDTO createMatchDTO) {
        // Validate teams exist
        Team homeTeam = teamRepository.findById(createMatchDTO.getHomeTeamId())
                .orElseThrow(TeamNotFoundException::new);

        Team awayTeam = teamRepository.findById(createMatchDTO.getAwayTeamId())
                .orElseThrow(TeamNotFoundException::new);

        Tournament tournament = tournamentRepository.findById(createMatchDTO.getTournamentId())
                .orElseThrow(TournamentNotFoundException::new);

        // Validate business rules
        if (homeTeam.getId().equals(awayTeam.getId())) {
            throw new SameTeamMatchException();
        }

        if (!tournament.getParticipatingTeams().contains(homeTeam) ||
                !tournament.getParticipatingTeams().contains(awayTeam)) {
            throw new TeamsNotInTournamentException();
        }

        // Validate scores
        if ((createMatchDTO.getHomeGoals() != null && createMatchDTO.getHomeGoals() < 0) ||
                (createMatchDTO.getAwayGoals() != null && createMatchDTO.getAwayGoals() < 0)) {
            throw new InvalidMatchResultException();
        }

        Match match = new Match();
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setTournament(tournament);
        match.setMatchDate(createMatchDTO.getMatchDate());
        match.setHomeGoals(createMatchDTO.getHomeGoals());
        match.setAwayGoals(createMatchDTO.getAwayGoals());
        match.setStatus(createMatchDTO.getStatus() != null ? createMatchDTO.getStatus() : MatchStatus.SCHEDULED);

        Match savedMatch = matchRepository.save(match);
        return convertToDTO(savedMatch);
    }

    public Optional<MatchDTO> updateMatch(Long id, UpdateMatchDTO updateMatchDTO) {
        return matchRepository.findById(id)
                .map(match -> {
                    // Validate scores
                    if ((updateMatchDTO.getHomeGoals() != null && updateMatchDTO.getHomeGoals() < 0) ||
                            (updateMatchDTO.getAwayGoals() != null && updateMatchDTO.getAwayGoals() < 0)) {
                        throw new InvalidMatchResultException();
                    }

                    // Update fields
                    if (updateMatchDTO.getMatchDate() != null)
                        match.setMatchDate(updateMatchDTO.getMatchDate());
                    if (updateMatchDTO.getHomeGoals() != null)
                        match.setHomeGoals(updateMatchDTO.getHomeGoals());
                    if (updateMatchDTO.getAwayGoals() != null)
                        match.setAwayGoals(updateMatchDTO.getAwayGoals());
                    if (updateMatchDTO.getStatus() != null)
                        match.setStatus(updateMatchDTO.getStatus());

                    return convertToDTO(matchRepository.save(match));
                });
    }

    public Optional<MatchDTO> updateResult(Long id, Integer homeGoals, Integer awayGoals) {
        if (homeGoals < 0 || awayGoals < 0) {
            throw new InvalidMatchResultException();
        }

        return matchRepository.findById(id)
                .map(match -> {
                    match.setHomeGoals(homeGoals);
                    match.setAwayGoals(awayGoals);
                    match.setStatus(MatchStatus.COMPLETED);

                    return convertToDTO(matchRepository.save(match));
                });
    }

    public boolean deleteMatch(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new MatchNotFoundException();
        }

        matchRepository.deleteById(id);
        return true;
    }

    public List<MatchDTO> getMatchesByTournament(Long tournamentId) {
        // Verify tournament exists
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new TournamentNotFoundException();
        }

        return matchRepository.findByTournamentId(tournamentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MatchDTO> getMatchesByTeam(Long teamId) {
        // Verify team exists
        if (!teamRepository.existsById(teamId)) {
            throw new TeamNotFoundException();
        }

        return matchRepository.findByTeamId(teamId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MatchDTO> getMatchesByStatus(MatchStatus status) {
        return matchRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MatchDTO> getMatchesByPeriod(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidationException("Start date cannot be after end date");
        }

        return matchRepository.findByMatchDateBetween(start, end).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MatchDTO> getTodayMatches() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        return getMatchesByPeriod(startOfDay, endOfDay);
    }

    private MatchDTO convertToDTO(Match match) {
        MatchDTO dto = new MatchDTO();
        dto.setId(match.getId());
        dto.setHomeTeamId(match.getHomeTeam().getId());
        dto.setAwayTeamId(match.getAwayTeam().getId());
        dto.setTournamentId(match.getTournament().getId());
        dto.setHomeTeamName(match.getHomeTeam().getName());
        dto.setAwayTeamName(match.getAwayTeam().getName());
        dto.setTournamentName(match.getTournament().getName());
        dto.setMatchDate(match.getMatchDate());
        dto.setHomeGoals(match.getHomeGoals());
        dto.setAwayGoals(match.getAwayGoals());
        dto.setStatus(match.getStatus());
        dto.setCreatedAt(match.getCreatedAt());
        dto.setResult(match.getResult());
        return dto;
    }
}