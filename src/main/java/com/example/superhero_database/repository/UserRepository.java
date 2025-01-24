package com.example.superhero_database.repository;

import com.example.superhero_database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


/**
 * Repository interface for User entity operations.
 *
 * @Repository marks this as a Spring Data repository
 * JpaRepository<User, Long> indicates:
 * - User: the entity type this repository manages
 * - Long: the type of the entity's primary key
 *
 * By extending JpaRepository, we automatically get methods like:
 * - save()
 * - findById()
 * - findAll()
 * - delete()
 * and many more without having to implement them
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     Custom method to find a user by username.
     * Spring Data JPA will automatically implement this method based on its name.
     *
     * The method name format 'findBy[FieldName]' tells Spring to:
     * 1. Create a query looking for User
     * 2. Match the username field with the provided parameter
     *
     * @param username the username to search for
     * @return Optional<User> because the user might not exist
     */
    Optional<User> findByUsername(String username);

    /**
     Custom method to find a user by email.
     * Spring Data JPA will automatically implement this method based on its email.
     *
     * The method name format 'findBy[FieldName]' tells Spring to:
     * 1. Create a query looking for User
     * 2. Match the email field with the provided parameter
     *
     * @param email the email to search for
     * @return Optional<User> because the user might not exist
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a username already exists.
     * Used during registration to prevent duplicate usernames.
     *
     * The 'existsBy[FieldName]' naming pattern creates a more efficient query
     * than finding the whole user object when we only need to know if it exists.
     */
    boolean existsByUsername(String username);

    /**
     * Similar to existsByUsername, but for email.
     * Ensures email uniqueness during registration.
     */
    boolean existsByEmail(String email);
}
