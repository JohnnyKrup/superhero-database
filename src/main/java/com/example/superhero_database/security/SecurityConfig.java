package com.example.superhero_database.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.authentication.ProviderManager;


import java.util.List;

/**
 * Central security configuration class for the application.
 * The SecurityConfig is often considered the "heart" of your application's security.
 * It's the difference between:
 * - A secure, properly functioning application
 * - A completely inaccessible application
 * - Or worse, an unsecured application exposing sensitive data
 *
 * @Configuration: Marks this as a configuration class
 * @EnableWebSecurity: Enables Spring Security's web security support
 * @EnableMethodSecurity: Enables method-level security using annotations like @PreAuthorize
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Constructor injection of required components.
     * Spring automatically provides these dependencies.
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configures and provides the AuthenticationManager bean for Spring Security.
     *
     * WHAT IT DOES:
     * - Creates a provider that knows how to authenticate users using username/password
     * - Configures this provider with our UserDetailsService (to find users)
     * - Configures it with our PasswordEncoder (to verify passwords)
     *
     * WHY IT'S NEEDED:
     * - Spring Security needs an AuthenticationManager to handle login attempts
     * - Without it, the system wouldn't know how to verify users' credentials
     *
     * WHAT TO KNOW:
     * - DaoAuthenticationProvider: Default provider that uses a UserDetailsService
     * - ProviderManager: The default AuthenticationManager implementation
     *
     * WHAT TO ACCEPT:
     * - The ProviderManager and DaoAuthenticationProvider classes come from Spring
     * - The setup pattern (provider → manager) is standard Spring Security structure
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Configures the main security filter chain.
     * This is where we define:
     * - Which endpoints are public/private
     * - How authentication is handled
     * - What security features to enable/disable
     *
     * IMPORTANT: The order of rules matters! More specific rules should come first.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // Disable CSRF protection because we're using JWT tokens
                // CSRF is not necessary for stateless REST APIs
                .csrf(AbstractHttpConfigurer::disable)
                // Configure access rules for different endpoints
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication needed)
                        .requestMatchers("/api/auth/**").permitAll()
                        // superheroAPI data
                         .requestMatchers("/api/superheroapi/**").permitAll()
                        // placeholderAPI for Placeholder images
                        .requestMatchers("/api/placeholder/**").permitAll()
                        // dashboard for authenticated users
                        .requestMatchers("/api/dashboard").authenticated()
                        // battle API can only be accessed when authenticated
                        .requestMatchers("/api/battle/**").authenticated()
                        // dashboard for authenticated users updating their profile
                        .requestMatchers("/api/users/profile", "/api/users/password").authenticated()
                        // Admin only user management
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        // Admin-only endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Any other endpoint requires authentication
                        .anyRequest().authenticated()
                        //.anyRequest().permitAll() // Allow all requests
                )
                // Configure session management
                .sessionManagement(session -> session
                // STATELESS means no session will be created or used
                // This is appropriate for REST APIs using JWT
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Set up the authentication provider
                .authenticationProvider(authenticationProvider())
                // Add our JWT filter before Spring's UsernamePassword filter
                // This ensures JWT authentication happens before attempting username/password auth
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // Register CORS configuration source
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Build the configuration
                .build();

    }

    /**
     * Configures the authentication provider.
     * This tells Spring Security how to load user details and check passwords.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Set the service that will fetch user details
        provider.setUserDetailsService(userDetailsService);
        // Set the encoder that will check passwords
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Defines the password encoder to use throughout the application.
     * BCrypt is a strong hashing function designed for passwords.
     *
     * IMPORTANT: This same encoder must be used:
     * 1. When creating new users (to hash their passwords)
     * 2. When checking passwords during login
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Configure CORS to allow requests from the frontend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Allow frontend origin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow standard REST methods
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
        configuration.setAllowCredentials(true); // Allow cookies or Authorization headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply CORS config to all endpoints
        return source;
    }
}