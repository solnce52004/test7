package dev.example.test7.exception.custom_exceptions.auth;

public class ResetPasswordException extends RuntimeException {
    public ResetPasswordException(String message) {
        super(message);
    }

    public ResetPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
