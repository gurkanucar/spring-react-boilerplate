package com.gucardev.springreactboilerplate.features.core.user.adapter.out.security;

import com.gucardev.springreactboilerplate.features.core.user.application.port.out.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing {@link PasswordEncoderPort} by delegating to the configured Spring Security
 * {@code PasswordEncoder}. Confines that dependency to a single adapter.
 */
@Component
@RequiredArgsConstructor
public class PasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
