package com.example.superhero_database.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filter intercepts every HTTP request to check if it has a valid JWT token.
 *
 * IMPORTANT CONCEPTS TO UNDERSTAND:
 * - Filters in Spring are like checkpoints that every request must pass through
 * - This filter runs once for each request (that's why it extends OncePerRequestFilter)
 * - It checks the Authorization header for a valid JWT token
 * - Non-authenticated requests can still proceed (might be public endpoints)
 * - If the token is valid, it sets up the Spring Security context
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Step 1: Skip filtering for public endpoints
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/api/auth/")) {
            // Public endpoints like /api/auth/** don't need token validation
            filterChain.doFilter(request, response);
            return;
        }


        // MUST KNOW: JWT tokens are sent in the Authorization header
        // The format is: "Bearer <token>"
        final String authHeader = request.getHeader("Authorization");

        // STEP 2: Check if the Authorization header exists and has the right format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // If no token or wrong format, continue to the next filter
            // This doesn't mean the request is rejected - it might be a public endpoint
            filterChain.doFilter(request, response);
            return;
        }

        // STEP 3: Extract the actual token
        // Skip "Bearer " (7 characters) to get the actual token
        final String jwt = authHeader.substring(7);

        // STEP 4: Extract the email from the token
        final String userEmail = jwtService.extractUseremail(jwt);
        System.out.println("UserEmail: " + userEmail);

        // STEP 5: Verify the token IF the user isn't already authenticated
        // SecurityContextHolder.getContext().getAuthentication() == null means the user isn't authenticated yet
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // STEP 5.1: Load the user details
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            System.out.println("UserDetails: " + userDetails);

            // STEP 5.2: Validate the token
            if (jwtService.isTokenValid(jwt, userEmail)) {
                // STEP 5.3: Create an authentication token
                // This object tells Spring Security that the user is authenticated
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // credentials (password) - null because we don't need it after authentication
                        userDetails.getAuthorities() // user's roles/authorities
                );

                System.out.println("isToken valid: " + authToken);

                // STEP 5.4: Add request details to the authentication token
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // STEP 5.5: Update the SecurityContext with the new authentication token
                // This is what marks the user as authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // STEP 6: Continue the filter chain
        // This is VERY IMPORTANT - it allows the request to continue to the next filter
        // or to the actual endpoint if all filters are done
        filterChain.doFilter(request, response);
    }
}