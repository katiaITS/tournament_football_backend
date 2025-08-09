package com.tournament_football_backend.service;

import com.tournament_football_backend.dto.CreateTeamDTO;
import com.tournament_football_backend.dto.TeamDTO;
import com.tournament_football_backend.dto.UpdateTeamDTO;
import com.tournament_football_backend.exception.TeamExceptions.*;
import com.tournament_football_backend.exception.UserExceptions.*;
import com.tournament_football_backend.exception.ValidationExceptions.*;
import com.tournament_football_backend.model.Role;
import com.tournament_football_backend.model.Team;
import com.tournament_football_backend.model.User;
import com.tournament_football_backend.repository.TeamRepository;
import com.tournament_football_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TeamService teamService;

    private Team testTeam;
    private User testUser;
    private CreateTeamDTO createTeamDTO;
    private UpdateTeamDTO updateTeamDTO;

    @BeforeEach
    void setUp() {
        testTeam = new Team();
        testTeam.setId(1L);
        testTeam.setName("Test Team");
        testTeam.setCreatedAt(LocalDateTime.now());
        testTeam.setPlayers(new HashSet<>());

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.ROLE_USER);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setTeams(new HashSet<>());

        createTeamDTO = new CreateTeamDTO();
        createTeamDTO.setName("New Team");

        updateTeamDTO = new UpdateTeamDTO();
        updateTeamDTO.setName("Updated Team");
    }

    @Test
    void getAllTeams_ShouldReturnListOfTeamDTOs() {
        // Given
        List<Team> teams = Arrays.asList(testTeam);
        when(teamRepository.findAll()).thenReturn(teams);

        // When
        List<TeamDTO> result = teamService.getAllTeams();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Team", result.get(0).getName());
        assertEquals(0, result.get(0).getNumberOfPlayers());
        verify(teamRepository).findAll();
    }

    @Test
    void getTeamById_WhenTeamExists_ShouldReturnTeamDTO() {
        // Given
        when(teamRepository.findByIdWithPlayers(1L)).thenReturn(Optional.of(testTeam));

        // When
        Optional<TeamDTO> result = teamService.getTeamById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Team", result.get().getName());
        assertEquals(testTeam.getId(), result.get().getId());
        verify(teamRepository).findByIdWithPlayers(1L);
    }

    @Test
    void getTeamById_WhenTeamNotExists_ShouldReturnEmpty() {
        // Given
        when(teamRepository.findByIdWithPlayers(1L)).thenReturn(Optional.empty());

        // When
        Optional<TeamDTO> result = teamService.getTeamById(1L);

        // Then
        assertTrue(result.isEmpty());
        verify(teamRepository).findByIdWithPlayers(1L);
    }

    @Test
    void getTeamByName_WhenTeamExists_ShouldReturnTeamDTO() {
        // Given
        when(teamRepository.findByName("Test Team")).thenReturn(Optional.of(testTeam));

        // When
        Optional<TeamDTO> result = teamService.getTeamByName("Test Team");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Team", result.get().getName());
        assertEquals(testTeam.getId(), result.get().getId());
        verify(teamRepository).findByName("Test Team");
    }

    @Test
    void getTeamByName_WhenTeamNotExists_ShouldReturnEmpty() {
        // Given
        when(teamRepository.findByName("Non Existent Team")).thenReturn(Optional.empty());

        // When
        Optional<TeamDTO> result = teamService.getTeamByName("Non Existent Team");

        // Then
        assertTrue(result.isEmpty());
        verify(teamRepository).findByName("Non Existent Team");
    }

    @Test
    void createTeam_WhenValidData_ShouldCreateAndReturnTeamDTO() {
        // Given
        when(teamRepository.existsByName("New Team")).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        // When
        TeamDTO result = teamService.createTeam(createTeamDTO);

        // Then
        assertNotNull(result);
        assertEquals("Test Team", result.getName());
        assertEquals(testTeam.getId(), result.getId());
        verify(teamRepository).existsByName("New Team");
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void createTeam_WhenNameAlreadyExists_ShouldThrowTeamNameAlreadyExistsException() {
        // Given
        when(teamRepository.existsByName("New Team")).thenReturn(true);

        // When & Then
        TeamNameAlreadyExistsException exception = assertThrows(TeamNameAlreadyExistsException.class,
                () -> teamService.createTeam(createTeamDTO));

        assertEquals("Team name already exists", exception.getMessage());
        assertEquals("TEAM_NAME_ALREADY_EXISTS", exception.getErrorCode());
        verify(teamRepository).existsByName("New Team");
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void updateTeam_WhenValidData_ShouldUpdateAndReturnTeamDTO() {
        // Given
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(teamRepository.existsByName("Updated Team")).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        // When
        Optional<TeamDTO> result = teamService.updateTeam(1L, updateTeamDTO);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTeam.getId(), result.get().getId());
        verify(teamRepository).findById(1L);
        verify(teamRepository).existsByName("Updated Team");
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void updateTeam_WhenTeamNotExists_ShouldReturnEmpty() {
        // Given
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<TeamDTO> result = teamService.updateTeam(1L, updateTeamDTO);

        // Then
        assertTrue(result.isEmpty());
        verify(teamRepository).findById(1L);
        verify(teamRepository, never()).existsByName(any());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void updateTeam_WhenNameAlreadyExists_ShouldThrowTeamNameAlreadyExistsException() {
        // Given
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(teamRepository.existsByName("Updated Team")).thenReturn(true);

        // When & Then
        TeamNameAlreadyExistsException exception = assertThrows(TeamNameAlreadyExistsException.class,
                () -> teamService.updateTeam(1L, updateTeamDTO));

        assertEquals("Team name already exists", exception.getMessage());
        assertEquals("TEAM_NAME_ALREADY_EXISTS", exception.getErrorCode());
        verify(teamRepository).findById(1L);
        verify(teamRepository).existsByName("Updated Team");
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void updateTeam_WhenSameName_ShouldNotCheckExistence() {
        // Given
        updateTeamDTO.setName("Test Team"); // Same as current name
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        // When
        Optional<TeamDTO> result = teamService.updateTeam(1L, updateTeamDTO);

        // Then
        assertTrue(result.isPresent());
        verify(teamRepository).findById(1L);
        verify(teamRepository, never()).existsByName(any());
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void updateTeam_WhenNullName_ShouldNotUpdateName() {
        // Given
        updateTeamDTO.setName(null);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        // When
        Optional<TeamDTO> result = teamService.updateTeam(1L, updateTeamDTO);

        // Then
        assertTrue(result.isPresent());
        verify(teamRepository).findById(1L);
        verify(teamRepository, never()).existsByName(any());
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void deleteTeam_WhenTeamExists_ShouldReturnTrue() {
        // Given
        when(teamRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = teamService.deleteTeam(1L);

        // Then
        assertTrue(result);
        verify(teamRepository).existsById(1L);
        verify(teamRepository).deleteById(1L);
    }

    @Test
    void deleteTeam_WhenTeamNotExists_ShouldThrowTeamNotFoundException() {
        // Given
        when(teamRepository.existsById(1L)).thenReturn(false);

        // When & Then
        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class,
                () -> teamService.deleteTeam(1L));

        assertEquals("Team not found", exception.getMessage());
        assertEquals("TEAM_NOT_FOUND", exception.getErrorCode());
        verify(teamRepository).existsById(1L);
        verify(teamRepository, never()).deleteById(any());
    }

    @Test
    void addPlayer_WhenValidData_ShouldReturnTrue() {
        // Given
        when(teamRepository.findByIdWithPlayers(1L)).thenReturn(Optional.of(testTeam));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        // When
        boolean result = teamService.addPlayer(1L, 1L);

        // Then
        assertTrue(result);
        verify(teamRepository).findByIdWithPlayers(1L);
        verify(userRepository).findById(1L);
        verify(teamRepository).save(testTeam);
    }

    @Test
    void addPlayer_WhenTeamNotExists_ShouldThrowTeamNotFoundException() {
        // Given
        when(teamRepository.findByIdWithPlayers(1L)).thenReturn(Optional.empty());

        // When & Then
        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class,
                () -> teamService.addPlayer(1L, 1L));

        assertEquals("Team not found", exception.getMessage());
        assertEquals("TEAM_NOT_FOUND", exception.getErrorCode());
        verify(teamRepository).findByIdWithPlayers(1L);
        verify(userRepository, never()).findById(any());
        verify(teamRepository, never()).save(any());
    }

    @Test
    void addPlayer_WhenUserNotExists_ShouldThrowUserNotFoundException() {
        // Given
        when(teamRepository.findByIdWithPlayers(1L)).thenReturn(Optional.of(testTeam));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> teamService.addPlayer(1L, 1L));

        assertEquals("User not found", exception.getMessage());
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        verify(teamRepository).findByIdWithPlayers(1L);
        verify(userRepository).findById(1L);
        verify(teamRepository, never()).save(any());
    }

    @Test
    void addPlayer_WhenPlayerAlreadyInTeam_ShouldThrowPlayerAlreadyInTeamException() {
        // Given
        testTeam.getPlayers().add(testUser);
        when(teamRepository.findByIdWithPlayers(1L)).thenReturn(Optional.of(testTeam));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        PlayerAlreadyInTeamException exception = assertThrows(PlayerAlreadyInTeamException.class,
                () -> teamService.addPlayer(1L, 1L));

        assertEquals("Player already in team", exception.getMessage());
        assertEquals("PLAYER_ALREADY_IN_TEAM", exception.getErrorCode());
        verify(teamRepository).findByIdWithPlayers(1L);
        verify(userRepository).findById(1L);
        verify(teamRepository, never()).save(any());
    }

    @Test
    void removePlayer_WhenValidData_ShouldReturnTrue() {
        // Given
        testTeam.getPlayers().add(testUser);
        testUser.getTeams().add(testTeam);
        when(teamRepository.findByIdWithPlayers(1L)).thenReturn(Optional.of(testTeam));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        // When
        boolean result = teamService.removePlayer(1L, 1L);

        // Then
        assertTrue(result);
        verify(teamRepository).findByIdWithPlayers(1L);
        verify(userRepository).findById(1L);
        verify(teamRepository).save(testTeam);
    }

    @Test
    void removePlayer_WhenTeamNotExists_ShouldThrowTeamNotFoundException() {
        // Given
        when(teamRepository.findByIdWithPlayers(1L)).thenReturn(Optional.empty());

        // When & Then
        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class,
                () -> teamService.removePlayer(1L, 1L));

        assertEquals("Team not found", exception.getMessage());
        assertEquals("TEAM_NOT_FOUND", exception.getErrorCode());
        verify(teamRepository).findByIdWithPlayers(1L);
        verify(userRepository, never()).findById(any());
        verify(teamRepository, never()).save(any());
    }

    @Test
    void removePlayer_WhenUserNotExists_ShouldThrowUserNotFoundException() {
        // Given
        when(teamRepository.findByIdWithPlayers(1L)).thenReturn(Optional.of(testTeam));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> teamService.removePlayer(1L, 1L));

        assertEquals("User not found", exception.getMessage());
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        verify(teamRepository).findByIdWithPlayers(1L);
        verify(userRepository).findById(1L);
        verify(teamRepository, never()).save(any());
    }

    @Test
    void removePlayer_WhenPlayerNotInTeam_ShouldThrowPlayerNotInTeamException() {
        // Given
        when(teamRepository.findByIdWithPlayers(1L)).thenReturn(Optional.of(testTeam));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        PlayerNotInTeamException exception = assertThrows(PlayerNotInTeamException.class,
                () -> teamService.removePlayer(1L, 1L));

        assertEquals("Player not in team", exception.getMessage());
        assertEquals("PLAYER_NOT_IN_TEAM", exception.getErrorCode());
        verify(teamRepository).findByIdWithPlayers(1L);
        verify(userRepository).findById(1L);
        verify(teamRepository, never()).save(any());
    }

    @Test
    void getTeamsByPlayer_WhenPlayerExists_ShouldReturnTeamsList() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(teamRepository.findByPlayerId(1L)).thenReturn(Arrays.asList(testTeam));

        // When
        List<TeamDTO> result = teamService.getTeamsByPlayer(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Team", result.get(0).getName());
        assertEquals(testTeam.getId(), result.get(0).getId());
        verify(userRepository).existsById(1L);
        verify(teamRepository).findByPlayerId(1L);
    }

    @Test
    void getTeamsByPlayer_WhenPlayerNotExists_ShouldThrowUserNotFoundException() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> teamService.getTeamsByPlayer(1L));

        assertEquals("User not found", exception.getMessage());
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        verify(userRepository).existsById(1L);
        verify(teamRepository, never()).findByPlayerId(any());
    }

    @Test
    void searchTeams_WhenValidKeyword_ShouldReturnTeamsList() {
        // Given
        when(teamRepository.findByNameContaining("Test")).thenReturn(Arrays.asList(testTeam));

        // When
        List<TeamDTO> result = teamService.searchTeams("Test");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Team", result.get(0).getName());
        assertEquals(testTeam.getId(), result.get(0).getId());
        verify(teamRepository).findByNameContaining("Test");
    }

    @Test
    void searchTeams_WhenEmptyKeyword_ShouldThrowEmptySearchKeywordException() {
        // When & Then
        EmptySearchKeywordException exception1 = assertThrows(EmptySearchKeywordException.class,
                () -> teamService.searchTeams(""));
        EmptySearchKeywordException exception2 = assertThrows(EmptySearchKeywordException.class,
                () -> teamService.searchTeams("   "));

        assertEquals("Search keyword cannot be empty", exception1.getMessage());
        assertEquals("EMPTY_SEARCH_KEYWORD", exception1.getErrorCode());
        assertEquals("Search keyword cannot be empty", exception2.getMessage());
        assertEquals("EMPTY_SEARCH_KEYWORD", exception2.getErrorCode());
        verify(teamRepository, never()).findByNameContaining(any());
    }

    @Test
    void searchTeams_WhenNullKeyword_ShouldThrowEmptySearchKeywordException() {
        // When & Then
        EmptySearchKeywordException exception = assertThrows(EmptySearchKeywordException.class,
                () -> teamService.searchTeams(null));

        assertEquals("Search keyword cannot be empty", exception.getMessage());
        assertEquals("EMPTY_SEARCH_KEYWORD", exception.getErrorCode());
        verify(teamRepository, never()).findByNameContaining(any());
    }

    @Test
    void searchTeams_WhenNoResults_ShouldReturnEmptyList() {
        // Given
        when(teamRepository.findByNameContaining("NonExistent")).thenReturn(Collections.emptyList());

        // When
        List<TeamDTO> result = teamService.searchTeams("NonExistent");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(teamRepository).findByNameContaining("NonExistent");
    }
}