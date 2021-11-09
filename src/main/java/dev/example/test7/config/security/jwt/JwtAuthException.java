package dev.example.test7.config.security.jwt;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthException extends AuthenticationException {
    private HttpStatus httpStatus;

    public JwtAuthException(String msg, Throwable e, HttpStatus httpStatus) {
        super(msg, e);
        this.httpStatus = httpStatus;
    }

    public JwtAuthException(String msg) {
        super(msg);
    }
}
