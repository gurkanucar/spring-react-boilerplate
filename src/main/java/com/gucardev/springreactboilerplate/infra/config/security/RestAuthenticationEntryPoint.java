package com.gucardev.springreactboilerplate.infra.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Returns a 401 {@link com.gucardev.springreactboilerplate.infra.exception.model.ApiError}
 * when an unauthenticated request hits a protected endpoint (replaces the default empty-body
 * 401 / HTTP-Basic challenge).
 */
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityErrorResponder responder;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        responder.write(response, HttpStatus.UNAUTHORIZED,
                "AUTHENTICATION_REQUIRED", "error.auth.unauthenticated");
    }
}
