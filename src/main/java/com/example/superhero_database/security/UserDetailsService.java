package com.example.superhero_database.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * This interface is provided by Spring Security.
 * It's the bridge between your user data and Spring Security's user authentication system.
 *
 * You don't need to create this interface - it comes from:
 * org.springframework.security.core.userdetails.UserDetailsService
 *
 * Just make sure to import it in your UserDetailsServiceImpl.
 */
public interface UserDetailsService {
    /**
     * This is the only method you must implement.
     * It's called by Spring Security whenever it needs to check user credentials.
     *
     * @param email the email to look up
     * @return a UserDetails object containing the user's security information
     * @throws UsernameNotFoundException if the user isn't found
     */
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    UserDetails loadUserByUseremail(String email) throws UsernameNotFoundException;
}