package com.example.superhero_database.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * This service handles all JWT (JSON Web Token) operations.
 *
 * IMPORTANT CONCEPTS TO UNDERSTAND:
 * - JWT is a standard for creating tokens that assert some number of claims
 * - They are used for authentication & authorization in web applications
 * - A JWT token consists of three parts: header.payload.signature
 * - The signature ensures that the token hasn't been altered
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserDetailsService userDetailsService;

    // MUST KNOW: This key is used to sign the JWT. In a real application,
    // this should be stored securely (e.g., in environment variables)
    // and should be at least 256 bits long
    @Value("${jwt.secret}")
    private String secretKey;

    @PostConstruct
    public void checkEnv() {
        System.out.println("System env JWT_SECRET_KEY: " + System.getenv("JWT_SECRET_KEY"));
        System.out.println("Spring value jwt.secret: " + secretKey);
    }

    /**
     * Extracts the username from a token.
     * MUST KNOW: This is used to verify who the token belongs to
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the useremail from a token.
     * MUST KNOW: This is used to verify who the token belongs to
     */
    public String extractUseremail(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        System.out.println("Token subject: " + subject);
        return subject;
    }

    /**
     * Creates a token for a specific user without any extra claims.
     * This is the method you'll use most often.
     *
     * COPY & USE: You can use this method as is when you need to create a token
     */
    public String generateToken(String email) {
        return generateToken(new HashMap<>(), email);
    }

    /**
     * Creates a token with extra claims (additional data you want to include).
     * In this case I want to add the roles to the token, so that I can check
     * protected routes in the Frontend, to ensure only ROLE_ADMIN can access
     * admin routes
     *
     * UNDERSTANDING THE PARTS:
     * - setClaims: adds any extra information you want to include
     * - setSubject: sets who the token belongs to user Email
     * - setIssuedAt: when the token was created
     * - setExpiration: when the token will expire
     * - signWith: signs the token with our secret key
     */
    private String generateToken(Map<String,Object> extraClaims, String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hour validity
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates if a token is still valid for a given username.
     * MUST KNOW: This is crucial for security - it checks if:
     * 1. The token belongs to the correct user
     * 2. The token hasn't expired
     */
    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractUseremail(token);
        System.out.println("Extracted Email from Token: " + extractedEmail);
        return (extractedEmail.equals(email)) && !isTokenExpired(token);
    }

    // IMPLEMENTATION DETAIL: Helper method to check if token has expired
    // You don't need to fully understand this, just know it checks the expiration date
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // IMPLEMENTATION DETAIL: Gets the expiration date from the token
    private Date extractExpiration(String token) {

        // This is a method reference (shorthand for a lambda expression)
        // It's equivalent to: claims -> claims.getExpiration()
        // Or the more verbose:
        // new Function<Claims, Date>() {
        //     @Override
        //     public Date apply(Claims claims) {
        //         return claims.getExpiration();
        //     }
        // }
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract any claim from the token.
     * ADVANCED CONCEPT: Uses Java Functions for flexibility
     * Don't worry too much about understanding this part - it's just a helper method
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * IMPLEMENTATION DETAIL: Extracts all claims from a token
     * This is the low-level JWT parsing logic.
     * You don't need to understand this deeply - just know it reads the token content
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * SECURITY DETAIL: Creates the key used to sign the JWT
     * This is cryptographic stuff - you don't need to understand the details
     * Just know that it creates a secure key from our secret string
     */
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
