package com.gucardev.springreactboilerplate.features.core.auth.application.service;

import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.LogoutUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.LoadRefreshTokenPort;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.SaveRefreshTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Revokes the presented refresh token. Idempotent: an unknown or already-revoked token is a no-op,
 * so logout never leaks whether a token existed.
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {

    private final LoadRefreshTokenPort loadRefreshTokenPort;
    private final SaveRefreshTokenPort saveRefreshTokenPort;

    @Override
    @Transactional
    public void logout(String refreshToken) {
        loadRefreshTokenPort.findByToken(refreshToken).ifPresent(token -> {
            token.revoke();
            saveRefreshTokenPort.save(token);
        });
    }
}
