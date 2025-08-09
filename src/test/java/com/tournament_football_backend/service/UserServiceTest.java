package com.tournament_football_backend.service;

import com.tournament_football_backend.dto.ProfileDTO;
import com.tournament_football_backend.dto.UpdateUserDTO;
import com.tournament_football_backend.dto.UserDTO;
import com.tournament_football_backend.exception.UserExceptions.*;
import com.tournament_football_backend.exception.ValidationExceptions.*;
import com.tournament_football_backend.model.Profile;
import com.tournament_football_backend.model.Role;
import com.tournament_football_backend.model.User;
import com.tournament_football_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;
    private UpdateUserDTO updateUserDTO;
    private ProfileDTO profileDTO;
    private Profile testProfile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.ROLE_USER);
        testUser.setCreatedAt(LocalDateTime.now());

        testProfile = new Profile();
        testProfile.setId(1L);
        testProfile.setFirstName("Test");
        testProfile.setLastName("User");
        testProfile.setBirthDate(LocalDate.of(1990, 1, 1));
        testProfile.setPhone("1234567890");
        testProfile.setCity("Test City");
        testProfile.setBio("Test bio");
        testProfile.setUser(testUser);

        testUserDTO = new UserDTO();
        testUserDTO.setUsername("testuser");
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setRole(Role.ROLE_USER);

        updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("updateduser");
        updateUserDTO.setEmail("updated@example.com");
        updateUserDTO.setRole(Role.ROLE_ADMIN);

        profileDTO = new ProfileDTO();
        profileDTO.setFirstName("Updated");
        profileDTO.setLastName("User");
        profileDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        profileDTO.setPhone("0987654321");
        profileDTO.setCity("Updated City");
        profileDTO.setBio("Updated bio");
    }

    @Test
    void getAllUsers_ShouldReturnUsersList() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        // When
        List<UserDTO> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("test@example.com", result.get(0).getEmail());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserDTO() {
        // Given
        when(userRepository.findByIdWithProfile(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDTO> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
        assertEquals(testUser.getId(), result.get().getId());
        verify(userRepository).findByIdWithProfile(1L);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldReturnEmpty() {
        // Given
        when(userRepository.findByIdWithProfile(1L)).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository).findByIdWithProfile(1L);
    }

    @Test
    void getUserByUsername_WhenUserExists_ShouldReturnUserDTO() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDTO> result = userService.getUserByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getUserByUsername_WhenUserNotExists_ShouldReturnEmpty() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> result = userService.getUserByUsername("nonexistent");

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void createUser_WhenValidData_ShouldCreateAndReturnUserDTO() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.createUser(testUserDTO, "password");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(Role.ROLE_USER, result.getRole());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WhenNullRole_ShouldDefaultToRoleUser() {
        // Given
        testUserDTO.setRole(null);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.createUser(testUserDTO, "password");

        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WhenUsernameExists_ShouldThrowUsernameAlreadyExistsException() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        UsernameAlreadyExistsException exception = assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.createUser(testUserDTO, "password"));

        assertEquals("Username already exists", exception.getMessage());
        assertEquals("USERNAME_ALREADY_EXISTS", exception.getErrorCode());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_WhenEmailExists_ShouldThrowEmailAlreadyExistsException() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.createUser(testUserDTO, "password"));

        assertEquals("Email already exists", exception.getMessage());
        assertEquals("EMAIL_ALREADY_EXISTS", exception.getErrorCode());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WhenValidData_ShouldUpdateAndReturnUserDTO() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("updateduser")).thenReturn(false);
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        Optional<UserDTO> result = userService.updateUser(1L, updateUserDTO);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("updateduser");
        verify(userRepository).existsByEmail("updated@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserNotExists_ShouldReturnEmpty() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> result = userService.updateUser(1L, updateUserDTO);

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WhenSameUsername_ShouldNotCheckUsernameExistence() {
        // Given
        updateUserDTO.setUsername("testuser"); // Same as current username
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        Optional<UserDTO> result = userService.updateUser(1L, updateUserDTO);

        // Then
        assertTrue(result.isPresent());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository).existsByEmail("updated@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WhenSameEmail_ShouldNotCheckEmailExistence() {
        // Given
        updateUserDTO.setEmail("test@example.com"); // Same as current email
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("updateduser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        Optional<UserDTO> result = userService.updateUser(1L, updateUserDTO);

        // Then
        assertTrue(result.isPresent());
        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("updateduser");
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WhenUsernameExists_ShouldThrowUsernameAlreadyExistsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("updateduser")).thenReturn(true);

        // When & Then
        UsernameAlreadyExistsException exception = assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.updateUser(1L, updateUserDTO));

        assertEquals("Username already exists", exception.getMessage());
        assertEquals("USERNAME_ALREADY_EXISTS", exception.getErrorCode());
        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("updateduser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WhenEmailExists_ShouldThrowEmailAlreadyExistsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("updateduser")).thenReturn(false);
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(true);

        // When & Then
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.updateUser(1L, updateUserDTO));

        assertEquals("Email already exists", exception.getMessage());
        assertEquals("EMAIL_ALREADY_EXISTS", exception.getErrorCode());
        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("updateduser");
        verify(userRepository).existsByEmail("updated@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WhenPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        // Given
        UpdateUserDTO partialUpdate = new UpdateUserDTO();
        partialUpdate.setRole(Role.ROLE_ADMIN);
        // username and email are null

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        Optional<UserDTO> result = userService.updateUser(1L, partialUpdate);

        // Then
        assertTrue(result.isPresent());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldReturnTrue() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = userService.deleteUser(1L);

        // Then
        assertTrue(result);
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldThrowUserNotFoundException() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(1L));

        assertEquals("User not found", exception.getMessage());
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void updateProfile_WhenUserExistsWithExistingProfile_ShouldUpdateProfile() {
        // Given
        testUser.setProfile(testProfile);
        when(userRepository.findByIdWithProfile(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        Optional<UserDTO> result = userService.updateProfile(1L, profileDTO);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        verify(userRepository).findByIdWithProfile(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateProfile_WhenUserExistsWithoutProfile_ShouldCreateNewProfile() {
        // Given
        testUser.setProfile(null);
        when(userRepository.findByIdWithProfile(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        Optional<UserDTO> result = userService.updateProfile(1L, profileDTO);

        // Then
        assertTrue(result.isPresent());
        verify(userRepository).findByIdWithProfile(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateProfile_WhenUserNotExists_ShouldReturnEmpty() {
        // Given
        when(userRepository.findByIdWithProfile(1L)).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> result = userService.updateProfile(1L, profileDTO);

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository).findByIdWithProfile(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfile_WhenPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        // Given
        ProfileDTO partialProfile = new ProfileDTO();
        partialProfile.setFirstName("OnlyFirstName");
        // Other fields are null

        testUser.setProfile(testProfile);
        when(userRepository.findByIdWithProfile(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        Optional<UserDTO> result = userService.updateProfile(1L, partialProfile);

        // Then
        assertTrue(result.isPresent());
        verify(userRepository).findByIdWithProfile(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    void searchUsers_WhenValidKeyword_ShouldReturnUsersList() {
        // Given
        when(userRepository.findByUsernameContainingOrEmailContaining("test"))
                .thenReturn(Arrays.asList(testUser));

        // When
        List<UserDTO> result = userService.searchUsers("test");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("test@example.com", result.get(0).getEmail());
        verify(userRepository).findByUsernameContainingOrEmailContaining("test");
    }

    @Test
    void searchUsers_WhenEmptyKeyword_ShouldThrowEmptySearchKeywordException() {
        // When & Then
        EmptySearchKeywordException exception1 = assertThrows(EmptySearchKeywordException.class,
                () -> userService.searchUsers(""));
        EmptySearchKeywordException exception2 = assertThrows(EmptySearchKeywordException.class,
                () -> userService.searchUsers("   "));

        assertEquals("Search keyword cannot be empty", exception1.getMessage());
        assertEquals("EMPTY_SEARCH_KEYWORD", exception1.getErrorCode());
        assertEquals("Search keyword cannot be empty", exception2.getMessage());
        assertEquals("EMPTY_SEARCH_KEYWORD", exception2.getErrorCode());
        verify(userRepository, never()).findByUsernameContainingOrEmailContaining(any());
    }

    @Test
    void searchUsers_WhenNullKeyword_ShouldThrowEmptySearchKeywordException() {
        // When & Then
        EmptySearchKeywordException exception = assertThrows(EmptySearchKeywordException.class,
                () -> userService.searchUsers(null));

        assertEquals("Search keyword cannot be empty", exception.getMessage());
        assertEquals("EMPTY_SEARCH_KEYWORD", exception.getErrorCode());
        verify(userRepository, never()).findByUsernameContainingOrEmailContaining(any());
    }

    @Test
    void searchUsers_WhenNoResults_ShouldReturnEmptyList() {
        // Given
        when(userRepository.findByUsernameContainingOrEmailContaining("nonexistent"))
                .thenReturn(Collections.emptyList());

        // When
        List<UserDTO> result = userService.searchUsers("nonexistent");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findByUsernameContainingOrEmailContaining("nonexistent");
    }

    @Test
    void existsByUsername_WhenUsernameExists_ShouldReturnTrue() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When
        boolean result = userService.existsByUsername("testuser");

        // Then
        assertTrue(result);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void existsByUsername_WhenUsernameNotExists_ShouldReturnFalse() {
        // Given
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // When
        boolean result = userService.existsByUsername("nonexistent");

        // Then
        assertFalse(result);
        verify(userRepository).existsByUsername("nonexistent");
    }

    @Test
    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When
        boolean result = userService.existsByEmail("test@example.com");

        // Then
        assertTrue(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void existsByEmail_WhenEmailNotExists_ShouldReturnFalse() {
        // Given
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // When
        boolean result = userService.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(result);
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }

    @Test
    void convertToDTO_WhenUserWithProfile_ShouldIncludeProfileData() {
        // Given
        testUser.setProfile(testProfile);
        when(userRepository.findByIdWithProfile(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDTO> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        UserDTO userDTO = result.get();
        assertNotNull(userDTO.getProfile());
        assertEquals("Test", userDTO.getProfile().getFirstName());
        assertEquals("User", userDTO.getProfile().getLastName());
        assertEquals(testProfile.getId(), userDTO.getProfile().getId());
    }

    @Test
    void convertToDTO_WhenUserWithoutProfile_ShouldHaveNullProfile() {
        // Given
        testUser.setProfile(null);
        when(userRepository.findByIdWithProfile(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDTO> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        UserDTO userDTO = result.get();
        assertNull(userDTO.getProfile());
    }
}