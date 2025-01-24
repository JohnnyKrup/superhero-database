package com.example.superhero_database.model;

import com.example.superhero_database.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * Entity class representing a user in the system.
 *
 * Entity marks this class as a JPA entity, meaning it will be mapped to a database table
 * Data is a Lombok annotation that automatically generates getters, setters, toString, equals, and hashCode methods
 * NoArgsConstructor creates a constructor with no parameters (required by JPA)
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "users") // Explicitly name the table to avoid conflicts with SQL reserved words
public class User {

    /**
     * The primary key of the user.
     * Using Long instead of Integer because:
     * 1. It can store much larger numbers (up to 2^63 - 1 vs 2^31 - 1 for Integer)
     * 2. It's the recommended type for IDs in large applications
     * 3. Many frameworks and libraries expect Long for IDs
     *
     * GeneratedValue automatically generates unique values for new entities
     * IDENTITY strategy relies on the database's auto-increment functionality
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Column(unique = true) ensures no two users can have the same username
     * nullable = false means this field cannot be null in the database
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Password should never be stored in plain text in a real application
     * We'll add password encryption in the security configuration
     */
    @Column(nullable = false)
    private String password;

    /**
     * Unique email ensures each email can only be used once
     * This helps with password recovery and user identification
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Enumerated stores the enum as a STRING in the database
     * Using STRING instead of ORDINAL because:
     * 1. It's more readable in the database
     * 2. It's safer when adding new enum values (ORDINAL can break existing data)
     * Default role is set to ROLE_USER for new users
     */
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_USER;

}
