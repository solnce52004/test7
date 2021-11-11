package dev.example.test7.security.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
@Getter
public class JwtAuthException extends AuthenticationException {
    private HttpStatus httpStatus = HttpStatus.FORBIDDEN;

    public JwtAuthException(String msg, Throwable e, HttpStatus httpStatus) {
        super(msg, e);
        this.httpStatus = httpStatus;
    }

    public JwtAuthException(String msg) {
        super(msg);
    }
}
