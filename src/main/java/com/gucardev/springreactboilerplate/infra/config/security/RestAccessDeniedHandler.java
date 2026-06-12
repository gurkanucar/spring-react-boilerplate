package com.gucardev.springreactboilerplate.infra.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Returns a 403 {@link com.gucardev.springreactboilerplate.infra.exception.model.ApiError}
 * when an authenticated request lacks the required authority (replaces the default empty-body
 * 403 produced by the filter chain).
 */
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityErrorResponder responder;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        responder.write(response, HttpStatus.FORBIDDEN,
                "ACCESS_DENIED", "error.auth.forbidden");
    }
}
