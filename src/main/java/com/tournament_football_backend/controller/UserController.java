package com.tournament_football_backend.controller;

import com.tournament_football_backend.dto.ProfileDTO;
import com.tournament_football_backend.dto.UpdateUserDTO;
import com.tournament_football_backend.dto.UserDTO;
import com.tournament_football_backend.exception.UserExceptions;
import com.tournament_football_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* * UserController.java
 * Handles user-related operations such as listing, searching, retrieving, updating, and deleting users.
 * Access is restricted based on user roles (ADMIN or self).
 *
 * Endpoints:
 * - GET /api/users: List all users (ADMIN only)
 * - GET /api/users/search?keyword={keyword}: Search users by keyword (ADMIN only)
 * - GET /api/users/username/{username}: Get user by username (ADMIN or self)
 * - GET /api/users/{id}: Get user by ID (ADMIN or self)
 * - PUT /api/users/{id}/profile: Update user profile (ADMIN or self)
 * - PUT /api/users/{id}: Update user (ADMIN or self)
 * - DELETE /api/users/{id}: Delete user (ADMIN only)
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    // GET /api/users - List all users (ADMIN only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // GET /api/users/search?keyword={keyword} - Search users (ADMIN only)
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String keyword) {
        List<UserDTO> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    // GET /api/users/username/{username} - Get user by username (ADMIN or self)
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/users/{id} - Get user by ID (ADMIN or self)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id)
                .orElseThrow(() -> new UserExceptions.UserNotFoundException());
        return ResponseEntity.ok(user);
    }

    // PUT /api/users/{id}/profile - Update user profile (ADMIN or self)
    @PutMapping("/{id}/profile")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> updateProfile(@PathVariable Long id, @Valid @RequestBody ProfileDTO profileDTO) {
        return userService.updateProfile(id, profileDTO)
                .map(updatedUser -> ResponseEntity.ok(updatedUser))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/users/{id} - Update user (ADMIN or self)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        return userService.updateUser(id, updateUserDTO)
                .map(updatedUser -> ResponseEntity.ok(updatedUser))
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/users/{id} - Delete user (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}