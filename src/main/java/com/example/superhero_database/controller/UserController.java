package com.example.superhero_database.controller;

import com.example.superhero_database.dto.UserDTO;
import com.example.superhero_database.model.PasswordUpdateRequest;
import com.example.superhero_database.model.User;
import com.example.superhero_database.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User-related endpoints.
 * Handles HTTP requests related to User operations.
 *
 * @RestController combines @Controller and @ResponseBody
 * @RequestMapping("/api/users") sets the base URL for all endpoints in this controller
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Create a new user
     * POST /api/users
     *
     * @RequestBody converts JSON to User object
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {
        UserDTO createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     *
     * @PathVariable extracts value from URL path
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Get all users
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Update user
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody User user) {
        System.out.println("Passed in ID: " + id + " & User: " + user);
        user.setId(id);
        UserDTO updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> adminDeleteUser(@PathVariable Long id) {
        userService.adminDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteOwnAccount() {
        userService.deleteOwnAccount();
        return ResponseEntity.noContent().build();
    }

    /**
     * Get the current user
     * @return UserDTO
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    /**
     * Update the Username and Email
     * @param userDTO
     * @return
     */
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateProfile(userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Update the Password of the User
     * @param request
     * @return
     */
    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(request);
        return ResponseEntity.ok().build();
    }
}