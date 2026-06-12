package com.gucardev.springreactboilerplate.infra.config.aspect;

import com.gucardev.springreactboilerplate.infra.exception.model.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExceptionLoggingAspect {

    @AfterThrowing(
            pointcut = "@within(org.springframework.stereotype.Service)",
            throwing = "ex"
    )
    public void logException(JoinPoint joinPoint, Throwable ex) {

        if (ex instanceof BusinessException) {
            return;
        }

        MethodSignature sig = (MethodSignature) joinPoint.getSignature();

        log.error(
                "Unhandled exception in {}.{}",
                sig.getDeclaringType().getSimpleName(),
                sig.getName(),
                ex
        );
    }

}