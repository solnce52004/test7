package dev.example.test7.annotation.auth;

import org.springframework.security.access.prepost.PreFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreFilter(
        value = "filterObject != authentication.principal.username",
        filterTarget = "usernames"
)
public @interface PreFilterUsernamesExceptCurrent {
}
