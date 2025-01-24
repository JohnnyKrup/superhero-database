package com.example.superhero_database.model.enums;

/**
 * Enum representing the possible roles a user can have in the system.
 *
 * IMPORTANT SPRING SECURITY CONCEPTS:
 * - The prefix "ROLE_" is required by Spring Security's default configuration
 * - Without "ROLE_" prefix, hasRole('ADMIN') wouldn't work, you'd need hasAuthority('ADMIN') instead
 * - When using @PreAuthorize or @Secured annotations, Spring automatically adds the "ROLE_" prefix
 *   Example: @PreAuthorize("hasRole('ADMIN')") looks for ROLE_ADMIN
 */
public enum UserRole {
    /**
     * Regular user role - has basic privileges
     * Can:
     * - Create their own superheroes
     * - Edit their own superheroes
     * - View all superheroes
     */
    ROLE_USER,

    /**
     * Administrator role - has full system access
     * Can do everything ROLE_USER can, plus:
     * - Manage all superheroes (even ones they didn't create)
     * - Manage user accounts
     * - Access admin-only endpoints
     */
    ROLE_ADMIN
}

/*
 * Usage examples in Spring Security:
 *
 * In SecurityConfig:
 * .requestMatchers("/api/admin/**").hasRole("ADMIN")  // Spring adds "ROLE_" prefix automatically
 *
 * In Controllers:
 * @PreAuthorize("hasRole('ADMIN')")  // Spring adds "ROLE_" prefix automatically
 * @PreAuthorize("hasRole('USER')")   // Spring adds "ROLE_" prefix automatically
 */