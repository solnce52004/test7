package dev.example.test7.exception.custom_handlers;

import lombok.extern.log4j.Log4j2;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Log4j2
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(
            Throwable ex,
            Method method,
            Object... params
    ) {
        log.error("(_*_) Exception while executing with message {} ", ex.getMessage());
        log.error("(_*_) Exception happen in {} method ", method.getName());
    }
}
