package com.example.superhero_database.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) for user registration requests.
 *
 * Why a separate DTO?
 * - Separates incoming API requests from the User entity.
 * - Prevents direct manipulation of sensitive fields like `id` or `role`.
 * - Makes validation easier for specific fields (e.g., email format, password strength).
 *
 * @Data automatically generates getters, setters, equals, hashCode, and toString methods.
 * @NoArgsConstructor creates a parameterless constructor (useful for serialization frameworks like Jackson).
 */
@Data
@NoArgsConstructor
public class RegisterRequest {

    /**
     * Email address of the user.
     * Must be unique and valid. Validation will be added later.
     */
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;


    /**
     * Username chosen by the user.
     * This will be their unique identifier for logging in.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20,message = "Username must be between 3 and 20 characters")
    private String username;

    /**
     * Plain-text password provided by the user.
     * Note: Never store passwords in plain text in the database.
     * We'll encrypt this before saving it to the User entity.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    /**
     * First name of the user.
     * This is optional and used for personalization in some systems.
     */
    private String firstName;

    /**
     * Last name of the user.
     * Like `firstName`, this is optional.
     */
    private String lastName;
}
