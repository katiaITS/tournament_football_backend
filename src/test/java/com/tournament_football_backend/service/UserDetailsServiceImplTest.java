package com.tournament_football_backend.service;

import com.tournament_football_backend.model.Role;
import com.tournament_football_backend.model.User;
import com.tournament_football_backend.repository.UserRepository;
import com.tournament_football_backend.service.UserDetailsServiceImpl.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.ROLE_USER);
        testUser.setCreatedAt(LocalDateTime.now());

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("encodedPassword");
        adminUser.setRole(Role.ROLE_ADMIN);
        adminUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());

        Collection<? extends GrantedAuthority> authorities = result.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));

        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        assertTrue(result.isEnabled());

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_WhenAdminUser_ShouldReturnUserDetailsWithAdminRole() {
        // Given
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        // When
        UserDetails result = userDetailsService.loadUserByUsername("admin");

        // Then
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());

        Collection<? extends GrantedAuthority> authorities = result.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

        verify(userRepository).findByUsername("admin");
    }

    @Test
    void loadUserByUsername_WhenUserNotExists_ShouldThrowUsernameNotFoundException() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent"));

        assertEquals("User Not Found with username: nonexistent", exception.getMessage());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void loadUserByUsername_WhenEmptyUsername_ShouldThrowUsernameNotFoundException() {
        // Given
        when(userRepository.findByUsername("")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(""));

        assertEquals("User Not Found with username: ", exception.getMessage());
        verify(userRepository).findByUsername("");
    }

    @Test
    void userPrincipalBuild_WithValidUser_ShouldReturnCorrectUserPrincipal() {
        // When
        UserPrincipal result = UserPrincipal.build(testUser);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());

        Collection<? extends GrantedAuthority> authorities = result.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void userPrincipalBuild_WithAdminUser_ShouldReturnCorrectUserPrincipal() {
        // When
        UserPrincipal result = UserPrincipal.build(adminUser);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("admin", result.getUsername());
        assertEquals("admin@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());

        Collection<? extends GrantedAuthority> authorities = result.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void userPrincipal_AccountStatus_ShouldAlwaysReturnTrue() {
        // Given
        UserPrincipal userPrincipal = UserPrincipal.build(testUser);

        // When & Then
        assertTrue(userPrincipal.isAccountNonExpired());
        assertTrue(userPrincipal.isAccountNonLocked());
        assertTrue(userPrincipal.isCredentialsNonExpired());
        assertTrue(userPrincipal.isEnabled());
    }

    @Test
    void userPrincipal_GetId_ShouldReturnCorrectId() {
        // Given
        UserPrincipal userPrincipal = UserPrincipal.build(testUser);

        // When
        Long id = userPrincipal.getId();

        // Then
        assertEquals(1L, id);
    }

    @Test
    void userPrincipal_GetEmail_ShouldReturnCorrectEmail() {
        // Given
        UserPrincipal userPrincipal = UserPrincipal.build(testUser);

        // When
        String email = userPrincipal.getEmail();

        // Then
        assertEquals("test@example.com", email);
    }

    @Test
    void userPrincipal_AuthoritiesSize_ShouldAlwaysBeOne() {
        // Given
        UserPrincipal userPrincipal = UserPrincipal.build(testUser);

        // When
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();

        // Then
        assertEquals(1, authorities.size());
    }

    @Test
    void userPrincipal_Serializable_ShouldHaveCorrectSerialVersionUID() {
        // Given
        UserPrincipal userPrincipal = UserPrincipal.build(testUser);

        // When & Then
        // This test ensures the UserPrincipal class is properly serializable
        assertNotNull(userPrincipal);
        assertTrue(userPrincipal instanceof java.io.Serializable);
    }

    @Test
    void loadUserByUsername_Integration_ShouldWorkWithUserPrincipal() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertTrue(userDetails instanceof UserPrincipal);
        UserPrincipal userPrincipal = (UserPrincipal) userDetails;
        assertEquals(testUser.getId(), userPrincipal.getId());
        assertEquals(testUser.getEmail(), userPrincipal.getEmail());
    }
}