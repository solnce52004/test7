package dev.example.test7.exceptions.custom_handlers;

import dev.example.test7.exceptions.custom_exceptions.ThereIsNoSuchUserException;
import dev.example.test7.exceptions.error_bodies.ErrorBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ApiExceptionHandler {
    /**
     * Будет вызван конструктор переопредеенный в ThereIsNoSuchUserException
     * а после обьработка перейдет сюда и будут добавлены сообщения в ответ
     */
    @ExceptionHandler(ThereIsNoSuchUserException.class)
    public ResponseEntity<Object> customHandleNotFound(
            ThereIsNoSuchUserException ex
//            , WebRequest request
    ) {
        ErrorBody errors = new ErrorBody();
        errors.setTimestamp(LocalDateTime.now());
        errors.setStatus(HttpStatus.NOT_FOUND.value());
        errors.setCustomMessage(ex.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }
}
