package com.tournament_football_backend.service;

import com.tournament_football_backend.dto.CreateMatchDTO;
import com.tournament_football_backend.dto.MatchDTO;
import com.tournament_football_backend.dto.UpdateMatchDTO;
import com.tournament_football_backend.exception.MatchExceptions.*;
import com.tournament_football_backend.exception.TeamExceptions.*;
import com.tournament_football_backend.exception.TournamentExceptions.*;
import com.tournament_football_backend.exception.ValidationExceptions.*;
import com.tournament_football_backend.model.Match;
import com.tournament_football_backend.model.MatchStatus;
import com.tournament_football_backend.model.Team;
import com.tournament_football_backend.model.Tournament;
import com.tournament_football_backend.repository.MatchRepository;
import com.tournament_football_backend.repository.TeamRepository;
import com.tournament_football_backend.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private MatchService matchService;

    private Match testMatch;
    private CreateMatchDTO testCreateMatchDTO;
    private UpdateMatchDTO testUpdateMatchDTO;
    private Team homeTeam;
    private Team awayTeam;
    private Tournament testTournament;

    @BeforeEach
    void setUp() {
        // Setup teams
        homeTeam = new Team();
        homeTeam.setId(1L);
        homeTeam.setName("Home Team");

        awayTeam = new Team();
        awayTeam.setId(2L);
        awayTeam.setName("Away Team");

        // Setup tournament
        testTournament = new Tournament();
        testTournament.setId(1L);
        testTournament.setName("Test Tournament");
        testTournament.setParticipatingTeams(new HashSet<>());
        testTournament.getParticipatingTeams().add(homeTeam);
        testTournament.getParticipatingTeams().add(awayTeam);

        // Setup match
        testMatch = new Match();
        testMatch.setId(1L);
        testMatch.setHomeTeam(homeTeam);
        testMatch.setAwayTeam(awayTeam);
        testMatch.setTournament(testTournament);
        testMatch.setMatchDate(LocalDateTime.now().plusDays(1));
        testMatch.setStatus(MatchStatus.SCHEDULED);
        testMatch.setHomeGoals(0);
        testMatch.setAwayGoals(0);
        testMatch.setCreatedAt(LocalDateTime.now());

        // Setup CreateMatchDTO
        testCreateMatchDTO = new CreateMatchDTO();
        testCreateMatchDTO.setHomeTeamId(1L);
        testCreateMatchDTO.setAwayTeamId(2L);
        testCreateMatchDTO.setTournamentId(1L);
        testCreateMatchDTO.setMatchDate(LocalDateTime.now().plusDays(1));
        testCreateMatchDTO.setStatus(MatchStatus.SCHEDULED);
        testCreateMatchDTO.setHomeGoals(0);
        testCreateMatchDTO.setAwayGoals(0);

        // Setup UpdateMatchDTO
        testUpdateMatchDTO = new UpdateMatchDTO();
        testUpdateMatchDTO.setMatchDate(LocalDateTime.now().plusDays(2));
        testUpdateMatchDTO.setStatus(MatchStatus.IN_PROGRESS);
    }

    @Test
    void getAllMatches_ShouldReturnListOfMatchDTOs() {
        // Given
        List<Match> matches = Arrays.asList(testMatch);
        when(matchRepository.findAll()).thenReturn(matches);

        // When
        List<MatchDTO> result = matchService.getAllMatches();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMatch.getId(), result.get(0).getId());
        assertEquals(testMatch.getHomeTeam().getName(), result.get(0).getHomeTeamName());
        assertEquals(testMatch.getAwayTeam().getName(), result.get(0).getAwayTeamName());
        verify(matchRepository).findAll();
    }

    @Test
    void getMatchById_WhenExists_ShouldReturnMatchDTO() {
        // Given
        when(matchRepository.findById(1L)).thenReturn(Optional.of(testMatch));

        // When
        Optional<MatchDTO> result = matchService.getMatchById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testMatch.getId(), result.get().getId());
        assertEquals(testMatch.getHomeTeam().getId(), result.get().getHomeTeamId());
        assertEquals(testMatch.getAwayTeam().getId(), result.get().getAwayTeamId());
        verify(matchRepository).findById(1L);
    }

    @Test
    void getMatchById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(matchRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<MatchDTO> result = matchService.getMatchById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(matchRepository).findById(1L);
    }

    @Test
    void createMatch_WhenValidData_ShouldCreateAndReturnMatch() {
        // Given
        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(awayTeam));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(testTournament));
        when(matchRepository.save(any(Match.class))).thenReturn(testMatch);

        // When
        MatchDTO result = matchService.createMatch(testCreateMatchDTO);

        // Then
        assertNotNull(result);
        assertEquals(testCreateMatchDTO.getHomeTeamId(), result.getHomeTeamId());
        assertEquals(testCreateMatchDTO.getAwayTeamId(), result.getAwayTeamId());
        assertEquals(testCreateMatchDTO.getTournamentId(), result.getTournamentId());
        verify(teamRepository).findById(1L);
        verify(teamRepository).findById(2L);
        verify(tournamentRepository).findById(1L);
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    void createMatch_WhenHomeTeamNotFound_ShouldThrowTeamNotFoundException() {
        // Given
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class,
                () -> matchService.createMatch(testCreateMatchDTO));

        assertEquals("Team not found", exception.getMessage());
        assertEquals("TEAM_NOT_FOUND", exception.getErrorCode());
        verify(teamRepository).findById(1L);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void createMatch_WhenAwayTeamNotFound_ShouldThrowTeamNotFoundException() {
        // Given
        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class,
                () -> matchService.createMatch(testCreateMatchDTO));

        assertEquals("Team not found", exception.getMessage());
        assertEquals("TEAM_NOT_FOUND", exception.getErrorCode());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void createMatch_WhenTournamentNotFound_ShouldThrowTournamentNotFoundException() {
        // Given
        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(awayTeam));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class,
                () -> matchService.createMatch(testCreateMatchDTO));

        assertEquals("Tournament not found", exception.getMessage());
        assertEquals("TOURNAMENT_NOT_FOUND", exception.getErrorCode());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void createMatch_WhenSameTeam_ShouldThrowSameTeamMatchException() {
        // Given
        testCreateMatchDTO.setAwayTeamId(1L); // Same as home team
        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(testTournament));

        // When & Then
        SameTeamMatchException exception = assertThrows(SameTeamMatchException.class,
                () -> matchService.createMatch(testCreateMatchDTO));

        assertEquals("A team cannot play against itself", exception.getMessage());
        assertEquals("SAME_TEAM_MATCH", exception.getErrorCode());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void createMatch_WhenTeamNotInTournament_ShouldThrowTeamsNotInTournamentException() {
        // Given
        Team externalTeam = new Team();
        externalTeam.setId(3L);
        externalTeam.setName("External Team");

        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(3L)).thenReturn(Optional.of(externalTeam));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(testTournament));

        testCreateMatchDTO.setAwayTeamId(3L);

        // When & Then
        TeamsNotInTournamentException exception = assertThrows(TeamsNotInTournamentException.class,
                () -> matchService.createMatch(testCreateMatchDTO));

        assertEquals("Both teams must be registered in the tournament", exception.getMessage());
        assertEquals("TEAMS_NOT_IN_TOURNAMENT", exception.getErrorCode());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void createMatch_WhenNegativeHomeGoals_ShouldThrowInvalidMatchResultException() {
        // Given
        testCreateMatchDTO.setHomeGoals(-1);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(awayTeam));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(testTournament));

        // When & Then
        InvalidMatchResultException exception = assertThrows(InvalidMatchResultException.class,
                () -> matchService.createMatch(testCreateMatchDTO));

        assertEquals("Invalid match result", exception.getMessage());
        assertEquals("INVALID_MATCH_RESULT", exception.getErrorCode());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void createMatch_WhenNegativeAwayGoals_ShouldThrowInvalidMatchResultException() {
        // Given
        testCreateMatchDTO.setAwayGoals(-1);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(awayTeam));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(testTournament));

        // When & Then
        InvalidMatchResultException exception = assertThrows(InvalidMatchResultException.class,
                () -> matchService.createMatch(testCreateMatchDTO));

        assertEquals("Invalid match result", exception.getMessage());
        assertEquals("INVALID_MATCH_RESULT", exception.getErrorCode());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void updateMatch_WhenExists_ShouldUpdateAndReturn() {
        // Given
        when(matchRepository.findById(1L)).thenReturn(Optional.of(testMatch));
        when(matchRepository.save(any(Match.class))).thenReturn(testMatch);

        // When
        Optional<MatchDTO> result = matchService.updateMatch(1L, testUpdateMatchDTO);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testMatch.getId(), result.get().getId());
        verify(matchRepository).findById(1L);
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    void updateMatch_WhenPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        // Given
        UpdateMatchDTO partialUpdate = new UpdateMatchDTO();
        partialUpdate.setStatus(MatchStatus.COMPLETED);
        // matchDate remains null (not updated)

        when(matchRepository.findById(1L)).thenReturn(Optional.of(testMatch));
        when(matchRepository.save(any(Match.class))).thenReturn(testMatch);

        // When
        Optional<MatchDTO> result = matchService.updateMatch(1L, partialUpdate);

        // Then
        assertTrue(result.isPresent());
        verify(matchRepository).findById(1L);
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    void updateMatch_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(matchRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<MatchDTO> result = matchService.updateMatch(1L, testUpdateMatchDTO);

        // Then
        assertFalse(result.isPresent());
        verify(matchRepository).findById(1L);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void updateMatch_WhenNegativeScore_ShouldThrowInvalidMatchResultException() {
        // Given
        testUpdateMatchDTO.setHomeGoals(-1);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(testMatch));

        // When & Then
        InvalidMatchResultException exception = assertThrows(InvalidMatchResultException.class,
                () -> matchService.updateMatch(1L, testUpdateMatchDTO));

        assertEquals("Invalid match result", exception.getMessage());
        assertEquals("INVALID_MATCH_RESULT", exception.getErrorCode());
        verify(matchRepository).findById(1L);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void updateResult_WhenExists_ShouldUpdateScoreAndComplete() {
        // Given
        when(matchRepository.findById(1L)).thenReturn(Optional.of(testMatch));
        when(matchRepository.save(any(Match.class))).thenReturn(testMatch);

        // When
        Optional<MatchDTO> result = matchService.updateResult(1L, 3, 1);

        // Then
        assertTrue(result.isPresent());
        verify(matchRepository).findById(1L);
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    void updateResult_WhenNegativeHomeGoals_ShouldThrowInvalidMatchResultException() {
        // When & Then
        InvalidMatchResultException exception = assertThrows(InvalidMatchResultException.class,
                () -> matchService.updateResult(1L, -1, 1));

        assertEquals("Invalid match result", exception.getMessage());
        assertEquals("INVALID_MATCH_RESULT", exception.getErrorCode());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void updateResult_WhenNegativeAwayGoals_ShouldThrowInvalidMatchResultException() {
        // When & Then
        InvalidMatchResultException exception = assertThrows(InvalidMatchResultException.class,
                () -> matchService.updateResult(1L, 1, -1));

        assertEquals("Invalid match result", exception.getMessage());
        assertEquals("INVALID_MATCH_RESULT", exception.getErrorCode());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void updateResult_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(matchRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<MatchDTO> result = matchService.updateResult(1L, 3, 1);

        // Then
        assertFalse(result.isPresent());
        verify(matchRepository).findById(1L);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void deleteMatch_WhenExists_ShouldReturnTrue() {
        // Given
        when(matchRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = matchService.deleteMatch(1L);

        // Then
        assertTrue(result);
        verify(matchRepository).existsById(1L);
        verify(matchRepository).deleteById(1L);
    }

    @Test
    void deleteMatch_WhenNotExists_ShouldThrowMatchNotFoundException() {
        // Given
        when(matchRepository.existsById(1L)).thenReturn(false);

        // When & Then
        MatchNotFoundException exception = assertThrows(MatchNotFoundException.class,
                () -> matchService.deleteMatch(1L));

        assertEquals("Match not found", exception.getMessage());
        assertEquals("MATCH_NOT_FOUND", exception.getErrorCode());
        verify(matchRepository).existsById(1L);
        verify(matchRepository, never()).deleteById(1L);
    }

    @Test
    void getMatchesByTournament_WhenTournamentExists_ShouldReturnTournamentMatches() {
        // Given
        List<Match> matches = Arrays.asList(testMatch);
        when(tournamentRepository.existsById(1L)).thenReturn(true);
        when(matchRepository.findByTournamentId(1L)).thenReturn(matches);

        // When
        List<MatchDTO> result = matchService.getMatchesByTournament(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMatch.getId(), result.get(0).getId());
        verify(tournamentRepository).existsById(1L);
        verify(matchRepository).findByTournamentId(1L);
    }

    @Test
    void getMatchesByTournament_WhenTournamentNotExists_ShouldThrowTournamentNotFoundException() {
        // Given
        when(tournamentRepository.existsById(1L)).thenReturn(false);

        // When & Then
        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class,
                () -> matchService.getMatchesByTournament(1L));

        assertEquals("Tournament not found", exception.getMessage());
        assertEquals("TOURNAMENT_NOT_FOUND", exception.getErrorCode());
        verify(tournamentRepository).existsById(1L);
        verify(matchRepository, never()).findByTournamentId(1L);
    }

    @Test
    void getMatchesByTeam_WhenTeamExists_ShouldReturnTeamMatches() {
        // Given
        List<Match> matches = Arrays.asList(testMatch);
        when(teamRepository.existsById(1L)).thenReturn(true);
        when(matchRepository.findByTeamId(1L)).thenReturn(matches);

        // When
        List<MatchDTO> result = matchService.getMatchesByTeam(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMatch.getId(), result.get(0).getId());
        verify(teamRepository).existsById(1L);
        verify(matchRepository).findByTeamId(1L);
    }

    @Test
    void getMatchesByTeam_WhenTeamNotExists_ShouldThrowTeamNotFoundException() {
        // Given
        when(teamRepository.existsById(1L)).thenReturn(false);

        // When & Then
        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class,
                () -> matchService.getMatchesByTeam(1L));

        assertEquals("Team not found", exception.getMessage());
        assertEquals("TEAM_NOT_FOUND", exception.getErrorCode());
        verify(teamRepository).existsById(1L);
        verify(matchRepository, never()).findByTeamId(1L);
    }

    @Test
    void getMatchesByStatus_ShouldReturnMatchesByStatus() {
        // Given
        List<Match> matches = Arrays.asList(testMatch);
        when(matchRepository.findByStatus(MatchStatus.SCHEDULED)).thenReturn(matches);

        // When
        List<MatchDTO> result = matchService.getMatchesByStatus(MatchStatus.SCHEDULED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(MatchStatus.SCHEDULED, result.get(0).getStatus());
        verify(matchRepository).findByStatus(MatchStatus.SCHEDULED);
    }

    @Test
    void getMatchesByPeriod_WhenValidDates_ShouldReturnMatchesInPeriod() {
        // Given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);
        List<Match> matches = Arrays.asList(testMatch);
        when(matchRepository.findByMatchDateBetween(start, end)).thenReturn(matches);

        // When
        List<MatchDTO> result = matchService.getMatchesByPeriod(start, end);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMatch.getId(), result.get(0).getId());
        verify(matchRepository).findByMatchDateBetween(start, end);
    }

    @Test
    void getMatchesByPeriod_WhenStartAfterEnd_ShouldThrowValidationException() {
        // Given
        LocalDateTime start = LocalDateTime.now().plusDays(7);
        LocalDateTime end = LocalDateTime.now();

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> matchService.getMatchesByPeriod(start, end));

        assertEquals("Start date cannot be after end date", exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        verify(matchRepository, never()).findByMatchDateBetween(any(), any());
    }

    @Test
    void getTodayMatches_ShouldReturnTodayMatches() {
        // Given
        List<Match> matches = Arrays.asList(testMatch);
        when(matchRepository.findByMatchDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(matches);

        // When
        List<MatchDTO> result = matchService.getTodayMatches();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMatch.getId(), result.get(0).getId());
        verify(matchRepository).findByMatchDateBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}