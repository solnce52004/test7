package dev.example.test7.exception.custom_exceptions;

public class UploadException extends RuntimeException {
    public UploadException(String message) {
        super(message);
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
