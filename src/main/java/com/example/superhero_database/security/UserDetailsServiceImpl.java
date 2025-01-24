package com.example.superhero_database.security;

import com.example.superhero_database.model.User;
import com.example.superhero_database.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import  java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor injection of UserRepository.
     * Spring will automatically provide the repository instance.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This is the method that Spring Security calls whenever it
     * needs to check user credentials or load user information.
     *
     * IMPORTANT: This method must:
     * 1. Find the user in your database
     * 2. Convert your User entity into Spring Security's UserDetails
     * 3. Throw UsernameNotFoundException if user isn't found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Step 1: Find user in database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Step 2: Convert UserRole enum to SimpleGrantedAuthority
        // Since the enum values already include "ROLE_" prefix, we can use the .name() function and don't have to
        // prefix it with ROLE_ and the convert the enum to a String
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());


        // Step 3: Create and return Spring Security's UserDetails object
        /**
         * because the superhero demo project does not have a Role class
         * and only handles roles with an Enum, we can assign only 1 role per user
         * that's why here:
         * .authorities(Collections.singletonList(authority))
         * we use the Collections.singletonList()
         * compared to the Collectors.toList() in the slides
         */
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword()) // This should be already encrypted
                .authorities(Collections.singletonList(authority)) // only one role possible
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();

    }
}
