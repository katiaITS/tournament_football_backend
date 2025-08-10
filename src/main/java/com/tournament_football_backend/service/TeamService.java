package com.tournament_football_backend.service;

import com.tournament_football_backend.dto.CreateTeamDTO;
import com.tournament_football_backend.dto.TeamDTO;
import com.tournament_football_backend.dto.UpdateTeamDTO;
import com.tournament_football_backend.dto.UserDTO;
import com.tournament_football_backend.model.Team;
import com.tournament_football_backend.model.User;
import com.tournament_football_backend.repository.TeamRepository;
import com.tournament_football_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tournament_football_backend.exception.TeamExceptions.*;
import static com.tournament_football_backend.exception.UserExceptions.*;
import static com.tournament_football_backend.exception.ValidationExceptions.*;

/* * TeamService.java
 * Service class for managing teams in the tournament football application.
 * Provides methods to create, update, delete, and retrieve teams and their players.
 */
@Service
@Transactional
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<TeamDTO> getTeamById(Long id) {
        return teamRepository.findByIdWithPlayers(id)
                .map(this::convertToDTO);
    }

    public Optional<TeamDTO> getTeamByName(String name) {
        return teamRepository.findByName(name)
                .map(this::convertToDTO);
    }

    public TeamDTO createTeam(CreateTeamDTO createTeamDTO) {
        // Check if team name already exists
        if (teamRepository.existsByName(createTeamDTO.getName())) {
            throw new TeamNameAlreadyExistsException();
        }

        Team team = new Team();
        team.setName(createTeamDTO.getName());

        Team savedTeam = teamRepository.save(team);
        return convertToDTO(savedTeam);
    }

    public Optional<TeamDTO> updateTeam(Long id, UpdateTeamDTO updateTeamDTO) {
        return teamRepository.findById(id)
                .map(team -> {
                    // Check if new name is provided and different from current name
                    if (updateTeamDTO.getName() != null && !updateTeamDTO.getName().equals(team.getName())) {
                        // Check if new name already exists
                        if (teamRepository.existsByName(updateTeamDTO.getName())) {
                            throw new TeamNameAlreadyExistsException();
                        }
                        team.setName(updateTeamDTO.getName());
                    }

                    return convertToDTO(teamRepository.save(team));
                });
    }

    public boolean deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new TeamNotFoundException();
        }

        teamRepository.deleteById(id);
        return true;
    }

    public boolean addPlayer(Long teamId, Long playerId) {
        Team team = teamRepository.findByIdWithPlayers(teamId)
                .orElseThrow(TeamNotFoundException::new);

        User player = userRepository.findById(playerId)
                .orElseThrow(UserNotFoundException::new);

        // Check if player is already in the team
        if (team.getPlayers().contains(player)) {
            throw new PlayerAlreadyInTeamException();
        }

        team.addPlayer(player);
        teamRepository.save(team);
        return true;
    }

    public boolean removePlayer(Long teamId, Long playerId) {
        Team team = teamRepository.findByIdWithPlayers(teamId)
                .orElseThrow(TeamNotFoundException::new);

        User player = userRepository.findById(playerId)
                .orElseThrow(UserNotFoundException::new);

        // Check if player is actually in the team
        if (!team.getPlayers().contains(player)) {
            throw new PlayerNotInTeamException();
        }

        team.removePlayer(player);
        teamRepository.save(team);
        return true;
    }

    public List<TeamDTO> getTeamsByPlayer(Long playerId) {
        // Verify player exists
        if (!userRepository.existsById(playerId)) {
            throw new UserNotFoundException();
        }

        return teamRepository.findByPlayerId(playerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeamDTO> searchTeams(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new EmptySearchKeywordException();
        }

        return teamRepository.findByNameContaining(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TeamDTO convertToDTO(Team team) {
        TeamDTO dto = new TeamDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setCreatedAt(team.getCreatedAt());
        dto.setNumberOfPlayers(team.getPlayers().size());

        if (team.getPlayers() != null && !team.getPlayers().isEmpty()) {
            Set<UserDTO> playersDTO = team.getPlayers().stream()
                    .map(this::convertUserToDTO)
                    .collect(Collectors.toSet());
            dto.setPlayers(playersDTO);
        }

        return dto;
    }

    private UserDTO convertUserToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}