package com.gucardev.springreactboilerplate.infra.config.aspect;

import com.gucardev.springreactboilerplate.infra.exception.model.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@Aspect
@Component
public class ExceptionLoggingAspect {

    private static final int MAX_PARAM_LENGTH = 150;

    // Skip exceptions that are already handled & turned into structured HTTP responses
    private static final Set<String> IGNORED_PACKAGES = Set.of(
            "org.springframework.web",
            "org.springframework.security"
    );

    @AfterThrowing(
            pointcut = "@within(org.springframework.stereotype.Service)" +
                    " || @within(org.springframework.stereotype.Repository)",
            throwing = "ex"
    )
    public void logException(JoinPoint joinPoint, Throwable ex) {
        if (shouldSkip(ex)) return;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();

        log.error("Exception in {}.{}({}) — {}: {} | cause: {}",
                signature.getDeclaringType().getSimpleName(),
                signature.getName(),
                buildParamString(paramNames, paramValues),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                formatCauseChain(ex),
                ex);
    }

    private String formatCauseChain(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        Throwable cause = ex.getCause();
        int depth = 0;
        while (cause != null && depth < 6) {
            if (depth > 0) sb.append(" → ");
            sb.append(cause.getClass().getSimpleName()).append(": ").append(cause.getMessage());
            cause = cause.getCause();
            depth++;
        }
        return sb.isEmpty() ? "<none>" : sb.toString();
    }

    private boolean shouldSkip(Throwable ex) {
        if (ex instanceof BusinessException) return true;
        String pkg = ex.getClass().getPackageName();
        return IGNORED_PACKAGES.stream().anyMatch(pkg::startsWith);
    }

    private String buildParamString(String[] names, Object[] values) {
        if (names == null || names.length == 0) return "";
        return IntStream.range(0, names.length)
                .mapToObj(i -> names[i] + "=" + truncateParamValue(values[i]))
                .collect(java.util.stream.Collectors.joining(", "));
    }

    // Helper method to safely convert and truncate parameter values
    private String truncateParamValue(Object value) {
        if (value == null) {
            return "null";
        }

        String strValue = value.toString();

        if (strValue.length() > MAX_PARAM_LENGTH) {
            return strValue.substring(0, MAX_PARAM_LENGTH) + "...(truncated)";
        }

        return strValue;
    }
}