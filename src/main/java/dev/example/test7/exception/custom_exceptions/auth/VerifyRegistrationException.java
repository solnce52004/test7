package dev.example.test7.exception.custom_exceptions.auth;

public class VerifyRegistrationException extends RuntimeException {
    public VerifyRegistrationException(String message) {
        super(message);
    }

    public VerifyRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}