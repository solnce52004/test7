package dev.example.test7.exceptions.custom_exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.NOT_FOUND,
        reason = "There is no such user kdsugfh")
public class ThereIsNoSuchUserException extends RuntimeException {

    public ThereIsNoSuchUserException(Long id) {
        super("entity not found with id=" + id);
    }
}
