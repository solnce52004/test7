package dev.example.test7.exception.custom_exceptions.auth;

public class RegistrationAuthException extends RuntimeException {
    public RegistrationAuthException(String message) {
        super(message);
    }

    public RegistrationAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}