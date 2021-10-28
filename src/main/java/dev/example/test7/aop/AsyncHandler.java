package dev.example.test7.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

//@Aspect
//@Component
@Slf4j
public class AsyncHandler {

    @Around("@annotation(org.springframework.scheduling.annotation.Async)")
    private Object handle(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        } catch (Throwable e) {
            log.error("in ASYNC, method: " +
                    pjp.getSignature().toLongString() +
                    ", args: " +
//                    AppStringUtils.transformToWellFormattedJsonString(pjp.getArgs()) +
                    ", exception: "+ e, e);
            throw e;
        }
    }

}
