package com.tournament_football_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import com.tournament_football_backend.model.Role;

public class UpdateUserDTO {

    // All fields optional for partial updates
    @Size(min = 3, max = 50)
    private String username;

    @Email
    private String email;

    private Role role;

    // Constructors
    public UpdateUserDTO() {}

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}