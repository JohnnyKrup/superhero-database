package com.example.superhero_database.exception;

public class UserNotFoundException extends SuperheroApiException {
    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }
}
