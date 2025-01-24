package com.example.superhero_database.service.impl;

import com.example.superhero_database.dto.UserDTO;
import com.example.superhero_database.exception.InvalidPasswordException;
import com.example.superhero_database.exception.UserNotFoundException;
import com.example.superhero_database.model.PasswordUpdateRequest;
import com.example.superhero_database.model.User;
import com.example.superhero_database.model.enums.UserRole;
import com.example.superhero_database.repository.UserRepository;
import com.example.superhero_database.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserService interface.
 * Handles user-related business logic with proper security checks.
 *
 * IMPORTANT CONCEPTS:
 * 1. User Security: Users can only modify their own profiles (except admins)
 * 2. Password Handling: Passwords are always encrypted before saving
 * 3. Data Integrity: Using @Transactional to ensure atomic operations
 *
 * MUST KNOW: This service implements the principle of least privilege
 * - Users can only access/modify their own data
 * - Admins have full access
 * - Sensitive operations are protected
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /**
     * Required dependencies declared as final
     * MUST KNOW: PasswordEncoder is crucial for security
     * - Never store plain-text passwords
     * - Always encrypt passwords before saving
     */
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * Creates a new user with encrypted password
     * IMPORTANT: This method should only be called after validation
     * of unique username/email (typically handled in AuthController)
     */
    @Override
    @Transactional
    public UserDTO createUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User u =  userRepository.save(user);
        return new UserDTO(u);
    }

    /**
     * Retrieves a user by ID with proper security checks
     *
     * SECURITY PATTERN:
     * - Regular users can only access their own profiles
     * - Admins can access any profile
     *
     * @param id the user ID to retrieve
     * @return the userDTO if found and accessible
     * @throws SecurityException if the current user doesn't have permission
     * @throws EntityNotFoundException if the user doesn't exist
     */
    @Override
    public UserDTO getUserById(Long id) {
        return new UserDTO(getUserByIdInternal(id));
    }

    /**
     * The internal version of the GetUSerByID function that is needed
     * in the promoteToAdmin function
     * @param id the user ID to retrieve
     * @return the complete user object
     */
    private User getUserByIdInternal(Long id) {
        // Get the currently authenticated user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));

        // Get the requested user
        User requestedUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // Check if current user has permission to access this profile
        if (!currentUser.getId().equals(id) &&
                !currentUser.getRole().name().equals("ROLE_ADMIN")) {
            throw new SecurityException("You don't have permission to access this user profile");
        }

        return requestedUser;
    }

    /**
     * Retrieves a user by username
     * MUST KNOW: This method is used by the security system for authentication
     * Therefore, it doesn't need additional security checks
     */
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    /**
     * Retrieves a userDTO by username
     * This is the function to use for user data
     * that is used in responses to the frontend
     */
    public UserDTO getUserDTOByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new UserDTO(user))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Retrieves a user by its email
     * MUST KNOW: This method is used by the security system for authentication
     * Therefore, it doesn't need additional security checks
     *
     * .map(user -> new UserDTO(user)) could also be written as .map(UserDTO::new)
     */
    @Override
    public UserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new UserDTO(user))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    /**
     * Lists all users
     * SECURITY: This method should only be accessible to admins
     * (controlled at the controller level with @PreAuthorize("hasRole('ADMIN')")
     */
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Updates a user's profile with security checks
     *
     * IMPORTANT SECURITY CONSIDERATIONS:
     * 1. Users can only update their own profiles (except admins)
     * 2. Passwords are always encrypted when updated
     * 3. Role changes are only allowed by admins
     *
     * @param user the updated user information
     * @return the updated user
     * @throws SecurityException if the current user doesn't have permission
     */
    @Override
    @Transactional
    public UserDTO updateUser(User user) {
        // Get current user (admin) from security context
        String adminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));

        // Verify admin privileges
        if (!adminUser.getRole().name().equals("ROLE_ADMIN")) {
            throw new SecurityException("Only administrators can update other users");
        }

        // Get user to be updated
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Update user information
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }

        User savedUser = userRepository.save(existingUser);
        return new UserDTO(savedUser);
    }

    /**
     * The admin deletes a user account
     *
     * SECURITY RULES:
     * - Users can only delete their own accounts
     * - Admins can delete any account
     * - Cannot delete the last admin account
     *
     * @param id the ID of the user to delete
     * @throws SecurityException if the current user doesn't have permission
     */
    @Override
    @Transactional
    public void adminDeleteUser(Long id) {
        String adminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));

        if (!adminUser.getRole().name().equals("ROLE_ADMIN")) {
            throw new SecurityException("Only administrators can delete users");
        }

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (userToDelete.getRole().name().equals("ROLE_ADMIN")) {
            long adminCount = userRepository.findAll().stream()
                    .filter(u -> u.getRole().name().equals("ROLE_ADMIN"))
                    .count();
            if (adminCount <= 1) {
                throw new SecurityException("Cannot delete the last administrator account");
            }
        }

        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteOwnAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getRole().name().equals("ROLE_ADMIN")) {
            long adminCount = userRepository.findAll().stream()
                    .filter(u -> u.getRole().name().equals("ROLE_ADMIN"))
                    .count();
            if (adminCount <= 1) {
                throw new SecurityException("Cannot delete the last administrator account");
            }
        }

        userRepository.deleteById(user.getId());
    }

    @Override
    public UserDTO getCurrentUser() {
        User user = getCurrentAuthenticatedUser();
        return new UserDTO(user);
    }

    @Override
    public UserDTO updateProfile(UserDTO userDTO) {
        User user = getCurrentAuthenticatedUser();

        if (userDTO.username() != null) {
            user.setUsername(userDTO.username());
        }
        if (userDTO.email() != null) {
            user.setEmail(userDTO.email());
        }

        User updatedUser = userRepository.save(user);
        return new UserDTO(updatedUser);
    }



    @Override
    public void updatePassword(PasswordUpdateRequest request) {
        User user = getCurrentAuthenticatedUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Checks if a username is already taken
     * Used during registration and profile updates
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Checks if an email is already in use
     * Used during registration and profile updates
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Creates a new user with encrypted password
     * IMPORTANT: This method should only be called after validation
     * of unique username/email (typically handled in AuthController)
     */
    @Override
    public User promoteToAdmin(Long userId) {
        User user = getUserByIdInternal(userId);
        user.setRole(UserRole.ROLE_ADMIN);
        return userRepository.save(user);
    }

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }


}