package com.example.superhero_database.exception;

public class InvalidPasswordException extends SuperheroApiException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
