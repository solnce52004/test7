package dev.example.test7.exceptions;

import dev.example.test7.exceptions.error_bodies.ErrorBody;
import dev.example.test7.exceptions.error_mappers.MapperErrorFieldsByMethodArgumentNotValid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice // Аннотация обработки исключений Spring
@Log4j2
public class ExtendedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    // дескриптор ошибки для @Valid //будет выбрасываться при @valid
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        ErrorBody body = new ErrorBody();
        body.setTimestamp(LocalDateTime.now());
        body.setCustomMessage("дескриптор ошибки @Valid");
//        body.setDebugMessage(ex.getMessage());
        body.setStatus(status.value());
        body.setStatusName(status.name());
        body.setErrors(
                new MapperErrorFieldsByMethodArgumentNotValid()
                        .getErrors(ex)
        );

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }
}
