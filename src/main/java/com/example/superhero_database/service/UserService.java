package com.example.superhero_database.service;

import com.example.superhero_database.dto.UserDTO;
import com.example.superhero_database.model.PasswordUpdateRequest;
import com.example.superhero_database.model.User;

import java.util.List;

/**
 * Service interface for user-related operations.
 * Defines the business operations available for User entities.
 *
 * Using an interface allows us to:
 * 1. Define a contract for user operations
 * 2. Easily switch implementations if needed
 * 3. Follow interface segregation principle
 */
public interface UserService {
    UserDTO createUser(User user);
    UserDTO getCurrentUser();
    UserDTO getUserById(Long id);
    User getUserByUsername(String username);
    UserDTO getUserDTOByUsername(String username);
    UserDTO getUserByEmail(String email);
    User getUserEntityByEmail(String email);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(User user);
    UserDTO updateProfile(UserDTO userDTO);
    void updatePassword(PasswordUpdateRequest request);
    void adminDeleteUser(Long id);
    void deleteOwnAccount();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User promoteToAdmin(Long userId);
}
