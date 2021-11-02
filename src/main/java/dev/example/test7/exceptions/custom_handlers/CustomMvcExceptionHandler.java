package dev.example.test7.exceptions.custom_handlers;

import dev.example.test7.exceptions.custom_exceptions.UploadException;
import dev.example.test7.exceptions.error_bodies.ErrorBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomMvcExceptionHandler {

    /**
     * Для MVC выгоднее разместить обработчик в каждом контроллере,
     * тк везде требуется редирект на свою "главную",
     * а при использовании в рест запросах по умолчанию возвращать
     * стандартное json тело ответа
     */
    @ExceptionHandler(UploadException.class)
    public ResponseEntity<Object> uploadExceptionHandler(
            UploadException e
    ) {
        ErrorBody errors = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setStatus(HttpStatus.BAD_REQUEST.value())
                .setCustomMessage("*** uploadExceptionHandler ***: " + e.getMessage())
                .setDebugMessage(e.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}

