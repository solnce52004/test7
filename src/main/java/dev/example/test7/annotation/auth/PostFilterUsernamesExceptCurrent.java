package dev.example.test7.annotation.auth;

import org.springframework.security.access.prepost.PostFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PostFilter("filterObject !='admin@u.com'")
//@PostFilter("filterObject != authentication.principal.username")
public @interface PostFilterUsernamesExceptCurrent {
}
