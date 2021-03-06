package dev.example.test7.annotation.auth;

import org.springframework.security.access.prepost.PostAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PostAuthorize("hasAnyAuthority({'WRITE', 'ROLE_ADMIN'})")
public @interface PreAuthorityWriter {
}
