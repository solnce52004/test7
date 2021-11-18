package dev.example.test7.exception.custom_exceptions.auth;

public class LoginAuthException extends RuntimeException {
    public LoginAuthException(String message) {
        super(message);
    }

    public LoginAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}