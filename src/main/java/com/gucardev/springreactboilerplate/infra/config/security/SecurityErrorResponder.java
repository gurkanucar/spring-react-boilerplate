package com.gucardev.springreactboilerplate.infra.config.security;

import com.gucardev.springreactboilerplate.infra.config.message.MessageUtil;
import com.gucardev.springreactboilerplate.infra.exception.model.ApiError;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Writes the standard {@link ApiError} envelope to the raw {@link HttpServletResponse} for
 * security failures that happen in the filter chain — before {@code @RestControllerAdvice}
 * can run. Keeps filter-level 401/403 responses identical to handler-produced ones.
 */
@Component
@RequiredArgsConstructor
public class SecurityErrorResponder {

    // Jackson 3 mapper auto-configured by Spring Boot 4 (honors spring.jackson.* settings).
    private final ObjectMapper objectMapper;

    public void write(HttpServletResponse response, HttpStatus status, String code, String messageKey)
            throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ApiError body = ApiError.business(
                status.value(),
                MessageUtil.getMessage(messageKey),
                code,
                MDC.get("traceId"));

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
