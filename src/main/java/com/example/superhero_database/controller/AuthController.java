package com.example.superhero_database.controller;

import com.example.superhero_database.dto.UserDTO;
import com.example.superhero_database.model.LoginRequest;
import com.example.superhero_database.model.LoginResponse;
import com.example.superhero_database.model.RegisterRequest;
import com.example.superhero_database.security.JwtService;
import com.example.superhero_database.service.AuthService;
import com.example.superhero_database.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;


/**
 * Authentication Controller handling user registration and login.
 *
 * JWT (JSON Web Token) Overview:
 * - JWT is a compact, URL-safe means of representing claims between two parties
 * - The token structure is: header.payload.signature
 * - Once a user is logged in, each subsequent request must include the JWT token
 * - The token is typically sent in the Authorization header using the Bearer schema
 *   Example: Authorization: Bearer <token>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // Lombok annotation to create constructor for final fields
public class AuthController {

    // Dependencies are marked final to ensure they're initialized through constructor injection
    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;

    /**
     * User Registration Endpoint
     *
     * This endpoint:
     * 1. Validates the registration request
     * 2. Checks for existing users with same username/email
     * 3. Creates a new user account
     *
     * Security Note: Password hashing should be handled in the service layer,
     * never store plain-text passwords!
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // Step 1: Validate uniqueness of username and email
        // This prevents duplicate accounts and is a basic security measure
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken.");
        }
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use.");
        }

        // Step 2: Register the user
        // The service layer should handle password hashing using BCrypt or similar
        authService.registerUser(request);
        UserDTO userDTO = userService.getUserByEmail(request.getEmail());

        // Step 3: Retrieve the created user (as DTO to avoid sending sensitive data)
        return ResponseEntity.ok(userDTO);
    }

    /**
     * User Login Endpoint
     *
     * JWT Authentication Flow:
     * 1. User submits credentials (email/password)
     * 2. Server validates credentials
     * 3. Server generates JWT token if valid
     * 4. Token is sent back to client
     * 5. Client stores token (usually in localStorage or secure cookie)
     * 6. Client sends token with subsequent requests
     *
     * Note: The actual JWT configuration (secret key, expiration, etc.)
     * should be in a separate configuration file
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Step 1: Authenticate user credentials
            // This uses Spring Security's Authentication Manager under the hood
            Authentication authentication = authService.authenticateUser(request);
            // Step 2: Get user details for the response
            // We use DTO to avoid sending sensitive data like passwords
            UserDTO userDTO = userService.getUserByEmail(request.getEmail());
            // Step 3: Generate JWT token
            // The token will contain the user's email as the subject
            // You can add additional claims to the token if needed
            String token = jwtService.generateToken(authentication.getName());

            // Step 4: Return the token and user info
            // The client should store this token and send it in the Authorization header
            // for subsequent requests
            return ResponseEntity.ok(new LoginResponse(token, userDTO));
        } catch (AuthenticationException e) {
            // Important: Don't provide too detailed error messages in production
            // as they could help attackers
            System.out.println("Token generation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password!");
        }
    }

    /**
     * Test endpoint to verify the controller is working
     * This can be used to verify your setup without needing full authentication
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("AuthController is working");
    }

    /*
     * IMPORTANT SECURITY CONSIDERATIONS:
     *
     * 1. Token Storage:
     *    - Never store sensitive data in JWT payload (it's base64 encoded, not encrypted)
     *    - Store tokens securely on the client side (httpOnly cookies recommended)
     *
     * 2. Token Validation:
     *    - Always validate tokens on the server side
     *    - Check token expiration
     *    - Verify token signature
     *
     * 3. HTTPS:
     *    - Always use HTTPS in production
     *    - Never send tokens over non-secure connections
     *
     * 4. Token Expiration:
     *    - Set reasonable expiration times
     *    - Implement refresh token mechanism for better security
     *
     * Common Gotchas:
     * - Don't store tokens in localStorage (vulnerable to XSS)
     * - Don't put sensitive data in JWT payload
     * - Don't forget to validate tokens on every protected request
     * - Don't use weak secret keys for token signing
     */

}
