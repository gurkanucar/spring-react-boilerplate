package com.gucardev.springreactboilerplate.features.core.auth.adapter.out.security;

import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.AuthenticatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing {@link AuthenticatePort} by delegating to Spring Security's
 * {@code AuthenticationManager}. Confines that dependency to a single adapter.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationManagerAdapter implements AuthenticatePort {

    private final AuthenticationManager authenticationManager;

    @Override
    public void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }
}
