package com.example.superhero_database.dto;

import com.example.superhero_database.model.User;

/**
 * Data Transfer Object for User information
 * Provides a secure way to transfer user data without exposing sensitive fields
 */
public record UserDTO(String id, String username, String email) {

    /**
     * Converts User entity to UserDTO
     * Handles ID conversion to String and excludes sensitive data like password
     * @param user The User entity to convert
     */
    public UserDTO(User user) {
        this(user.getId().toString(),
                user.getUsername(),
                user.getEmail());
    }
}

