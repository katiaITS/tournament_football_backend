package com.tournament_football_backend.service;

import com.tournament_football_backend.dto.ProfileDTO;
import com.tournament_football_backend.dto.UpdateUserDTO;
import com.tournament_football_backend.dto.UserDTO;
import com.tournament_football_backend.model.Profile;
import com.tournament_football_backend.model.Role;
import com.tournament_football_backend.model.User;
import com.tournament_football_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tournament_football_backend.exception.UserExceptions.*;
import static com.tournament_football_backend.exception.ValidationExceptions.*;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findByIdWithProfile(id)
                .map(this::convertToDTO);
    }

    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDTO);
    }

    public UserDTO createUser(UserDTO userDTO, String password) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : Role.ROLE_USER);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public Optional<UserDTO> updateUser(Long id, UpdateUserDTO updateUserDTO) {
        return userRepository.findById(id)
                .map(user -> {
                    if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().equals(user.getUsername())) {
                        if (userRepository.existsByUsername(updateUserDTO.getUsername())) {
                            throw new UsernameAlreadyExistsException();
                        }
                        user.setUsername(updateUserDTO.getUsername());
                    }
                    if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail().equals(user.getEmail())) {
                        if (userRepository.existsByEmail(updateUserDTO.getEmail())) {
                            throw new EmailAlreadyExistsException();
                        }
                        user.setEmail(updateUserDTO.getEmail());
                    }
                    if (updateUserDTO.getRole() != null) {
                        user.setRole(updateUserDTO.getRole());
                    }
                    return convertToDTO(userRepository.save(user));
                });
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }

        userRepository.deleteById(id);
        return true;
    }

    public Optional<UserDTO> updateProfile(Long userId, ProfileDTO profileDTO) {
        return userRepository.findByIdWithProfile(userId)
                .map(user -> {
                    Profile profile = user.getProfile();
                    if (profile == null) {
                        profile = new Profile();
                        profile.setUser(user);
                        user.setProfile(profile);
                    }

                    if (profileDTO.getFirstName() != null) profile.setFirstName(profileDTO.getFirstName());
                    if (profileDTO.getLastName() != null) profile.setLastName(profileDTO.getLastName());
                    if (profileDTO.getBirthDate() != null) profile.setBirthDate(profileDTO.getBirthDate());
                    if (profileDTO.getPhone() != null) profile.setPhone(profileDTO.getPhone());
                    if (profileDTO.getCity() != null) profile.setCity(profileDTO.getCity());
                    if (profileDTO.getBio() != null) profile.setBio(profileDTO.getBio());

                    return convertToDTO(userRepository.save(user));
                });
    }

    public List<UserDTO> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new EmptySearchKeywordException();
        }

        return userRepository.findByUsernameContainingOrEmailContaining(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());

        if (user.getProfile() != null) {
            ProfileDTO profileDTO = new ProfileDTO();
            profileDTO.setId(user.getProfile().getId());
            profileDTO.setFirstName(user.getProfile().getFirstName());
            profileDTO.setLastName(user.getProfile().getLastName());
            profileDTO.setBirthDate(user.getProfile().getBirthDate());
            profileDTO.setPhone(user.getProfile().getPhone());
            profileDTO.setCity(user.getProfile().getCity());
            profileDTO.setBio(user.getProfile().getBio());
            dto.setProfile(profileDTO);
        }

        return dto;
    }
}