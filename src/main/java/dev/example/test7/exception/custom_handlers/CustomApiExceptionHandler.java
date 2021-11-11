package dev.example.test7.exception.custom_handlers;

import dev.example.config.security.exception.JwtAuthException;
import dev.example.test7.exception.custom_exceptions.ThereIsNoSuchUserException;
import dev.example.config.security.exception.TokenRefreshException;
import dev.example.test7.exception.error_bodies.ErrorBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomApiExceptionHandler {
    /**
     * Будет вызван конструктор переопределенный в ThereIsNoSuchUserException
     * а после обработки перейдет сюда и будут добавлены сообщения в ответ
     */
    @ExceptionHandler(ThereIsNoSuchUserException.class)
    public ResponseEntity<Object> customHandleNotFound(
            ThereIsNoSuchUserException ex,
            WebRequest request
    ) {
        ErrorBody errors = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setStatus(HttpStatus.NOT_FOUND.value())
                .setCustomMessage(ex.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JwtAuthException.class)
    public ResponseEntity<?> handleJwtAuthException(
            JwtAuthException ex,
            WebRequest request
    ) {
        ErrorBody errors = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setStatus(ex.getHttpStatus().value())
                .setCustomMessage(ex.getMessage())
                .setDebugMessage(ex.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<Object> handleTokenRefreshException(
            TokenRefreshException ex,
            WebRequest request
    ) {
        ErrorBody errors = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setStatus(HttpStatus.FORBIDDEN.value())
                .setCustomMessage(ex.getMessage())
                .setDebugMessage(ex.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }
}
