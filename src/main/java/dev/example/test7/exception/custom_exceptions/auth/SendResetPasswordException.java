package dev.example.test7.exception.custom_exceptions.auth;

public class SendResetPasswordException extends RuntimeException {
    public SendResetPasswordException(String message) {
        super(message);
    }

    public SendResetPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
