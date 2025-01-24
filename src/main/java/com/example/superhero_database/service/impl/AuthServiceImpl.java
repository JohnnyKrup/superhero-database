package com.example.superhero_database.service.impl;

import com.example.superhero_database.model.LoginRequest;
import com.example.superhero_database.model.RegisterRequest;
import com.example.superhero_database.model.User;
import com.example.superhero_database.model.enums.UserRole;
import com.example.superhero_database.security.UserDetailsServiceImpl;
import com.example.superhero_database.service.AuthService;
import com.example.superhero_database.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implements authentication and registration business logic.
 * Acts as a bridge between AuthController and security components.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // UserDetailsService loads user-specific data during authentication
    private final UserDetailsServiceImpl userDetailsService;
    // PasswordEncoder handles secure password hashing
    private final PasswordEncoder passwordEncoder;
    // AuthenticationManager validates user credentials
    private final AuthenticationManager authenticationManager;
    // UserService handles user data operations
    private final UserService userService;

    /**
     * Authenticates user credentials using Spring Security.
     *
     * Flow:
     * 1. Creates authentication token with credentials
     * 2. AuthenticationManager validates credentials
     * 3. If valid, returns Authentication object
     * 4. If invalid, throws AuthenticationException
     */
    @Override
    public Authentication authenticateUser(LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
    }

    /**
     * Registers a new user in the system.
     *
     * Flow:
     * 1. Maps RegisterRequest to User entity
     * 2. Sets default role (ROLE_USER)
     * 3. UserService handles password encryption and saving
     */
    @Override
    public void registerUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword()); // UserService handles encryption
        user.setRole(UserRole.ROLE_USER);

        userService.createUser(user);
    }

    /**
     * Checks if username already exists.
     * Delegates to UserService to maintain single source of truth.
     */
    @Override
    public boolean existsByUsername(String username) {
        return userService.existsByUsername(username);
    }

    /**
     * Checks if email already exists.
     * Delegates to UserService to maintain single source of truth.
     */
    @Override
    public boolean existsByEmail(String email) {
        return userService.existsByEmail(email);
    }
}