package com.tournament_football_backend.service;

import com.tournament_football_backend.dto.CreateTournamentDTO;
import com.tournament_football_backend.dto.TournamentDTO;
import com.tournament_football_backend.dto.UpdateTournamentDTO;
import com.tournament_football_backend.exception.TeamExceptions.*;
import com.tournament_football_backend.exception.TournamentExceptions.*;
import com.tournament_football_backend.exception.ValidationExceptions.*;
import com.tournament_football_backend.model.Team;
import com.tournament_football_backend.model.Tournament;
import com.tournament_football_backend.model.TournamentStatus;
import com.tournament_football_backend.repository.TeamRepository;
import com.tournament_football_backend.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TournamentService tournamentService;

    private Tournament testTournament;
    private Team testTeam;
    private CreateTournamentDTO createTournamentDTO;
    private UpdateTournamentDTO updateTournamentDTO;

    @BeforeEach
    void setUp() {
        testTournament = new Tournament();
        testTournament.setId(1L);
        testTournament.setName("Test Tournament");
        testTournament.setDescription("Test Description");
        testTournament.setStartDate(LocalDate.now().plusDays(1));
        testTournament.setEndDate(LocalDate.now().plusDays(7));
        testTournament.setMaxTeams(8);
        testTournament.setStatus(TournamentStatus.OPEN);
        testTournament.setCreatedAt(LocalDateTime.now());
        testTournament.setParticipatingTeams(new HashSet<>());

        testTeam = new Team();
        testTeam.setId(1L);
        testTeam.setName("Test Team");
        testTeam.setPlayers(new HashSet<>());
        testTeam.setCreatedAt(LocalDateTime.now());

        createTournamentDTO = new CreateTournamentDTO();
        createTournamentDTO.setName("New Tournament");
        createTournamentDTO.setDescription("New Description");
        createTournamentDTO.setStartDate(LocalDate.now().plusDays(1));
        createTournamentDTO.setEndDate(LocalDate.now().plusDays(7));
        createTournamentDTO.setMaxTeams(8);
        createTournamentDTO.setStatus(TournamentStatus.SCHEDULED);

        updateTournamentDTO = new UpdateTournamentDTO();
        updateTournamentDTO.setName("Updated Tournament");
    }

    @Test
    void getAllTournaments_ShouldReturnTournamentsList() {
        // Given
        when(tournamentRepository.findAll()).thenReturn(Arrays.asList(testTournament));

        // When
        List<TournamentDTO> result = tournamentService.getAllTournaments();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Tournament", result.get(0).getName());
        assertEquals(TournamentStatus.OPEN, result.get(0).getStatus());
        verify(tournamentRepository).findAll();
    }

    @Test
    void getTournamentById_WhenTournamentExists_ShouldReturnTournamentDTO() {
        // Given
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.of(testTournament));

        // When
        Optional<TournamentDTO> result = tournamentService.getTournamentById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Tournament", result.get().getName());
        assertEquals(TournamentStatus.OPEN, result.get().getStatus());
        assertEquals(testTournament.getId(), result.get().getId());
        verify(tournamentRepository).findByIdWithTeams(1L);
    }

    @Test
    void getTournamentById_WhenTournamentNotExists_ShouldReturnEmpty() {
        // Given
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.empty());

        // When
        Optional<TournamentDTO> result = tournamentService.getTournamentById(1L);

        // Then
        assertTrue(result.isEmpty());
        verify(tournamentRepository).findByIdWithTeams(1L);
    }

    @Test
    void createTournament_WhenValidData_ShouldCreateAndReturnTournamentDTO() {
        // Given
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(testTournament);

        // When
        TournamentDTO result = tournamentService.createTournament(createTournamentDTO);

        // Then
        assertNotNull(result);
        assertEquals("Test Tournament", result.getName());
        assertEquals(testTournament.getId(), result.getId());
        verify(tournamentRepository).save(any(Tournament.class));
    }

    @Test
    void createTournament_WhenScheduledStatus_ShouldCreateWithScheduledStatus() {
        // Given
        Tournament scheduledTournament = new Tournament();
        scheduledTournament.setId(2L);
        scheduledTournament.setName("New Tournament");
        scheduledTournament.setStatus(TournamentStatus.SCHEDULED);
        scheduledTournament.setCreatedAt(LocalDateTime.now());
        scheduledTournament.setParticipatingTeams(new HashSet<>());
        scheduledTournament.setStartDate(createTournamentDTO.getStartDate());
        scheduledTournament.setEndDate(createTournamentDTO.getEndDate());

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(scheduledTournament);

        // When
        TournamentDTO result = tournamentService.createTournament(createTournamentDTO);

        // Then
        assertNotNull(result);
        assertEquals("New Tournament", result.getName());
        assertEquals(TournamentStatus.SCHEDULED, result.getStatus());
        verify(tournamentRepository).save(any(Tournament.class));
    }

    @Test
    void createTournament_WhenNullStatus_ShouldDefaultToOpen() {
        // Given
        createTournamentDTO.setStatus(null);
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(testTournament);

        // When
        TournamentDTO result = tournamentService.createTournament(createTournamentDTO);

        // Then
        assertNotNull(result);
        verify(tournamentRepository).save(any(Tournament.class));
    }

    @Test
    void createTournament_WhenStartDateAfterEndDate_ShouldThrowInvalidTournamentDateException() {
        // Given
        createTournamentDTO.setStartDate(LocalDate.now().plusDays(7));
        createTournamentDTO.setEndDate(LocalDate.now().plusDays(1));

        // When & Then
        InvalidTournamentDateException exception = assertThrows(InvalidTournamentDateException.class,
                () -> tournamentService.createTournament(createTournamentDTO));

        assertEquals("Start date must be before end date", exception.getMessage());
        assertEquals("INVALID_TOURNAMENT_DATE", exception.getErrorCode());
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void updateTournament_WhenValidData_ShouldUpdateAndReturnTournamentDTO() {
        // Given
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(testTournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(testTournament);

        // When
        Optional<TournamentDTO> result = tournamentService.updateTournament(1L, updateTournamentDTO);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTournament.getId(), result.get().getId());
        verify(tournamentRepository).findById(1L);
        verify(tournamentRepository).save(any(Tournament.class));
    }

    @Test
    void updateTournament_WhenStatusUpdate_ShouldUpdateStatus() {
        // Given
        updateTournamentDTO.setStatus(TournamentStatus.IN_PROGRESS);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(testTournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(testTournament);

        // When
        Optional<TournamentDTO> result = tournamentService.updateTournament(1L, updateTournamentDTO);

        // Then
        assertTrue(result.isPresent());
        verify(tournamentRepository).findById(1L);
        verify(tournamentRepository).save(any(Tournament.class));
    }

    @Test
    void updateTournament_WhenInvalidDatesAfterUpdate_ShouldThrowInvalidTournamentDateException() {
        // Given
        updateTournamentDTO.setStartDate(LocalDate.now().plusDays(10));
        updateTournamentDTO.setEndDate(LocalDate.now().plusDays(5));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(testTournament));

        // When & Then
        InvalidTournamentDateException exception = assertThrows(InvalidTournamentDateException.class,
                () -> tournamentService.updateTournament(1L, updateTournamentDTO));

        assertEquals("Start date must be before end date", exception.getMessage());
        assertEquals("INVALID_TOURNAMENT_DATE", exception.getErrorCode());
        verify(tournamentRepository).findById(1L);
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void updateTournament_WhenTournamentNotExists_ShouldReturnEmpty() {
        // Given
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<TournamentDTO> result = tournamentService.updateTournament(1L, updateTournamentDTO);

        // Then
        assertTrue(result.isEmpty());
        verify(tournamentRepository).findById(1L);
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void deleteTournament_WhenTournamentExists_ShouldReturnTrue() {
        // Given
        when(tournamentRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = tournamentService.deleteTournament(1L);

        // Then
        assertTrue(result);
        verify(tournamentRepository).existsById(1L);
        verify(tournamentRepository).deleteById(1L);
    }

    @Test
    void deleteTournament_WhenTournamentNotExists_ShouldThrowTournamentNotFoundException() {
        // Given
        when(tournamentRepository.existsById(1L)).thenReturn(false);

        // When & Then
        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class,
                () -> tournamentService.deleteTournament(1L));

        assertEquals("Tournament not found", exception.getMessage());
        assertEquals("TOURNAMENT_NOT_FOUND", exception.getErrorCode());
        verify(tournamentRepository).existsById(1L);
        verify(tournamentRepository, never()).deleteById(any());
    }

    @Test
    void registerTeam_WhenValidData_ShouldReturnTrue() {
        // Given
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.of(testTournament));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(testTournament);

        // When
        boolean result = tournamentService.registerTeam(1L, 1L);

        // Then
        assertTrue(result);
        verify(tournamentRepository).findByIdWithTeams(1L);
        verify(teamRepository).findById(1L);
        verify(tournamentRepository).save(testTournament);
    }

    @Test
    void registerTeam_WhenTournamentNotExists_ShouldThrowTournamentNotFoundException() {
        // Given
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.empty());

        // When & Then
        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class,
                () -> tournamentService.registerTeam(1L, 1L));

        assertEquals("Tournament not found", exception.getMessage());
        assertEquals("TOURNAMENT_NOT_FOUND", exception.getErrorCode());
        verify(tournamentRepository).findByIdWithTeams(1L);
        verify(teamRepository, never()).findById(any());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void registerTeam_WhenTeamNotExists_ShouldThrowTeamNotFoundException() {
        // Given
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.of(testTournament));
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class,
                () -> tournamentService.registerTeam(1L, 1L));

        assertEquals("Team not found", exception.getMessage());
        assertEquals("TEAM_NOT_FOUND", exception.getErrorCode());
        verify(tournamentRepository).findByIdWithTeams(1L);
        verify(teamRepository).findById(1L);
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void registerTeam_WhenTournamentNotOpen_ShouldThrowTournamentNotOpenException() {
        // Given
        testTournament.setStatus(TournamentStatus.IN_PROGRESS);
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.of(testTournament));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));

        // When & Then
        TournamentNotOpenException exception = assertThrows(TournamentNotOpenException.class,
                () -> tournamentService.registerTeam(1L, 1L));

        assertEquals("Tournament is not open for registration", exception.getMessage());
        assertEquals("TOURNAMENT_NOT_OPEN", exception.getErrorCode());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void registerTeam_WhenTournamentCompleted_ShouldThrowTournamentNotOpenException() {
        // Given
        testTournament.setStatus(TournamentStatus.COMPLETED);
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.of(testTournament));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));

        // When & Then
        TournamentNotOpenException exception = assertThrows(TournamentNotOpenException.class,
                () -> tournamentService.registerTeam(1L, 1L));

        assertEquals("Tournament is not open for registration", exception.getMessage());
        assertEquals("TOURNAMENT_NOT_OPEN", exception.getErrorCode());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void registerTeam_WhenTournamentCancelled_ShouldThrowTournamentNotOpenException() {
        // Given
        testTournament.setStatus(TournamentStatus.CANCELLED);
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.of(testTournament));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));

        // When & Then
        TournamentNotOpenException exception = assertThrows(TournamentNotOpenException.class,
                () -> tournamentService.registerTeam(1L, 1L));

        assertEquals("Tournament is not open for registration", exception.getMessage());
        assertEquals("TOURNAMENT_NOT_OPEN", exception.getErrorCode());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void registerTeam_WhenTournamentScheduled_ShouldThrowTournamentNotOpenException() {
        // Given
        testTournament.setStatus(TournamentStatus.SCHEDULED);
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.of(testTournament));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));

        // When & Then
        TournamentNotOpenException exception = assertThrows(TournamentNotOpenException.class,
                () -> tournamentService.registerTeam(1L, 1L));

        assertEquals("Tournament is not open for registration", exception.getMessage());
        assertEquals("TOURNAMENT_NOT_OPEN", exception.getErrorCode());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void registerTeam_WhenTournamentFull_ShouldThrowTournamentFullException() {
        // Given
        // Fill tournament to maximum capacity
        for (int i = 0; i < testTournament.getMaxTeams(); i++) {
            Team team = new Team();
            team.setId((long) (i + 2)); // Start from ID 2
            team.setName("Team " + (i + 2));
            testTournament.getParticipatingTeams().add(team);
        }

        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.of(testTournament));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));

        // When & Then
        TournamentFullException exception = assertThrows(TournamentFullException.class,
                () -> tournamentService.registerTeam(1L, 1L));

        assertEquals("Tournament has reached maximum number of teams", exception.getMessage());
        assertEquals("TOURNAMENT_FULL", exception.getErrorCode());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void registerTeam_WhenTeamAlreadyInTournament_ShouldThrowTeamAlreadyInTournamentException() {
        // Given
        testTournament.getParticipatingTeams().add(testTeam);
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.of(testTournament));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));

        // When & Then
        TeamAlreadyInTournamentException exception = assertThrows(TeamAlreadyInTournamentException.class,
                () -> tournamentService.registerTeam(1L, 1L));

        assertEquals("Team is already registered in tournament", exception.getMessage());
        assertEquals("TEAM_ALREADY_IN_TOURNAMENT", exception.getErrorCode());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void removeTeam_WhenValidData_ShouldReturnTrue() {
        // Given
        testTournament.getParticipatingTeams().add(testTeam);
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.of(testTournament));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(testTournament);

        // When
        boolean result = tournamentService.removeTeam(1L, 1L);

        // Then
        assertTrue(result);
        verify(tournamentRepository).findByIdWithTeams(1L);
        verify(teamRepository).findById(1L);
        verify(tournamentRepository).save(testTournament);
    }

    @Test
    void removeTeam_WhenTournamentNotExists_ShouldThrowTournamentNotFoundException() {
        // Given
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.empty());

        // When & Then
        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class,
                () -> tournamentService.removeTeam(1L, 1L));

        assertEquals("Tournament not found", exception.getMessage());
        assertEquals("TOURNAMENT_NOT_FOUND", exception.getErrorCode());
        verify(teamRepository, never()).findById(any());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void removeTeam_WhenTeamNotExists_ShouldThrowTeamNotFoundException() {
        // Given
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.of(testTournament));
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class,
                () -> tournamentService.removeTeam(1L, 1L));

        assertEquals("Team not found", exception.getMessage());
        assertEquals("TEAM_NOT_FOUND", exception.getErrorCode());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void removeTeam_WhenTeamNotInTournament_ShouldThrowTeamNotInTournamentException() {
        // Given
        when(tournamentRepository.findByIdWithTeams(1L)).thenReturn(Optional.of(testTournament));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));

        // When & Then
        TeamNotInTournamentException exception = assertThrows(TeamNotInTournamentException.class,
                () -> tournamentService.removeTeam(1L, 1L));

        assertEquals("Team is not registered in tournament", exception.getMessage());
        assertEquals("TEAM_NOT_IN_TOURNAMENT", exception.getErrorCode());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void getTournamentsByStatus_WhenOpen_ShouldReturnFilteredList() {
        // Given
        when(tournamentRepository.findByStatus(TournamentStatus.OPEN))
                .thenReturn(Arrays.asList(testTournament));

        // When
        List<TournamentDTO> result = tournamentService.getTournamentsByStatus(TournamentStatus.OPEN);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TournamentStatus.OPEN, result.get(0).getStatus());
        verify(tournamentRepository).findByStatus(TournamentStatus.OPEN);
    }

    @Test
    void getTournamentsByStatus_WhenInProgress_ShouldReturnFilteredList() {
        // Given
        testTournament.setStatus(TournamentStatus.IN_PROGRESS);
        when(tournamentRepository.findByStatus(TournamentStatus.IN_PROGRESS))
                .thenReturn(Arrays.asList(testTournament));

        // When
        List<TournamentDTO> result = tournamentService.getTournamentsByStatus(TournamentStatus.IN_PROGRESS);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TournamentStatus.IN_PROGRESS, result.get(0).getStatus());
        verify(tournamentRepository).findByStatus(TournamentStatus.IN_PROGRESS);
    }

    @Test
    void getTournamentsByStatus_WhenCompleted_ShouldReturnFilteredList() {
        // Given
        testTournament.setStatus(TournamentStatus.COMPLETED);
        when(tournamentRepository.findByStatus(TournamentStatus.COMPLETED))
                .thenReturn(Arrays.asList(testTournament));

        // When
        List<TournamentDTO> result = tournamentService.getTournamentsByStatus(TournamentStatus.COMPLETED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TournamentStatus.COMPLETED, result.get(0).getStatus());
        verify(tournamentRepository).findByStatus(TournamentStatus.COMPLETED);
    }

    @Test
    void getTournamentsByStatus_WhenCancelled_ShouldReturnFilteredList() {
        // Given
        testTournament.setStatus(TournamentStatus.CANCELLED);
        when(tournamentRepository.findByStatus(TournamentStatus.CANCELLED))
                .thenReturn(Arrays.asList(testTournament));

        // When
        List<TournamentDTO> result = tournamentService.getTournamentsByStatus(TournamentStatus.CANCELLED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TournamentStatus.CANCELLED, result.get(0).getStatus());
        verify(tournamentRepository).findByStatus(TournamentStatus.CANCELLED);
    }

    @Test
    void getTournamentsByStatus_WhenScheduled_ShouldReturnFilteredList() {
        // Given
        testTournament.setStatus(TournamentStatus.SCHEDULED);
        when(tournamentRepository.findByStatus(TournamentStatus.SCHEDULED))
                .thenReturn(Arrays.asList(testTournament));

        // When
        List<TournamentDTO> result = tournamentService.getTournamentsByStatus(TournamentStatus.SCHEDULED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TournamentStatus.SCHEDULED, result.get(0).getStatus());
        verify(tournamentRepository).findByStatus(TournamentStatus.SCHEDULED);
    }

    @Test
    void getTournamentsByStatus_WhenNoTournamentsFound_ShouldReturnEmptyList() {
        // Given
        when(tournamentRepository.findByStatus(TournamentStatus.OPEN))
                .thenReturn(Collections.emptyList());

        // When
        List<TournamentDTO> result = tournamentService.getTournamentsByStatus(TournamentStatus.OPEN);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tournamentRepository).findByStatus(TournamentStatus.OPEN);
    }

    @Test
    void getUpcomingTournaments_ShouldReturnFutureTournaments() {
        // Given
        when(tournamentRepository.findByStartDateAfter(any(LocalDate.class)))
                .thenReturn(Arrays.asList(testTournament));

        // When
        List<TournamentDTO> result = tournamentService.getUpcomingTournaments();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getStartDate().isAfter(LocalDate.now()));
        verify(tournamentRepository).findByStartDateAfter(any(LocalDate.class));
    }

    @Test
    void getTournamentsByTeam_WhenTeamExists_ShouldReturnTournamentsList() {
        // Given
        when(teamRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.findByParticipatingTeamId(1L))
                .thenReturn(Arrays.asList(testTournament));

        // When
        List<TournamentDTO> result = tournamentService.getTournamentsByTeam(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTournament.getId(), result.get(0).getId());
        verify(teamRepository).existsById(1L);
        verify(tournamentRepository).findByParticipatingTeamId(1L);
    }

    @Test
    void getTournamentsByTeam_WhenTeamNotExists_ShouldThrowTeamNotFoundException() {
        // Given
        when(teamRepository.existsById(1L)).thenReturn(false);

        // When & Then
        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class,
                () -> tournamentService.getTournamentsByTeam(1L));

        assertEquals("Team not found", exception.getMessage());
        assertEquals("TEAM_NOT_FOUND", exception.getErrorCode());
        verify(teamRepository).existsById(1L);
        verify(tournamentRepository, never()).findByParticipatingTeamId(any());
    }

    @Test
    void searchTournaments_WhenValidKeyword_ShouldReturnTournamentsList() {
        // Given
        when(tournamentRepository.findByNameContaining("Test"))
                .thenReturn(Arrays.asList(testTournament));

        // When
        List<TournamentDTO> result = tournamentService.searchTournaments("Test");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Tournament", result.get(0).getName());
        assertEquals(testTournament.getId(), result.get(0).getId());
        verify(tournamentRepository).findByNameContaining("Test");
    }

    @Test
    void searchTournaments_WhenEmptyKeyword_ShouldThrowEmptySearchKeywordException() {
        // When & Then
        EmptySearchKeywordException exception1 = assertThrows(EmptySearchKeywordException.class,
                () -> tournamentService.searchTournaments(""));
        EmptySearchKeywordException exception2 = assertThrows(EmptySearchKeywordException.class,
                () -> tournamentService.searchTournaments("   "));

        assertEquals("Search keyword cannot be empty", exception1.getMessage());
        assertEquals("EMPTY_SEARCH_KEYWORD", exception1.getErrorCode());
        assertEquals("Search keyword cannot be empty", exception2.getMessage());
        assertEquals("EMPTY_SEARCH_KEYWORD", exception2.getErrorCode());
        verify(tournamentRepository, never()).findByNameContaining(any());
    }

    @Test
    void searchTournaments_WhenNullKeyword_ShouldThrowEmptySearchKeywordException() {
        // When & Then
        EmptySearchKeywordException exception = assertThrows(EmptySearchKeywordException.class,
                () -> tournamentService.searchTournaments(null));

        assertEquals("Search keyword cannot be empty", exception.getMessage());
        assertEquals("EMPTY_SEARCH_KEYWORD", exception.getErrorCode());
        verify(tournamentRepository, never()).findByNameContaining(any());
    }

    @Test
    void searchTournaments_WhenNoResults_ShouldReturnEmptyList() {
        // Given
        when(tournamentRepository.findByNameContaining("NonExistent"))
                .thenReturn(Collections.emptyList());

        // When
        List<TournamentDTO> result = tournamentService.searchTournaments("NonExistent");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tournamentRepository).findByNameContaining("NonExistent");
    }
}