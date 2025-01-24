package com.example.superhero_database.service;

import com.example.superhero_database.model.LoginRequest;
import com.example.superhero_database.model.RegisterRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {
    Authentication authenticateUser(LoginRequest loginRequest);
    void registerUser(RegisterRequest registerRequest);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}