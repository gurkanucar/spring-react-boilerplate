package com.gucardev.springreactboilerplate.features.core.auth.service.usecase;

import com.gucardev.springreactboilerplate.features.core.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Revokes the presented refresh token. Idempotent: an unknown or already-revoked token is a no-op,
 * so logout never leaks whether a token existed.
 */
@Service
@RequiredArgsConstructor
public class LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void execute(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }
}
