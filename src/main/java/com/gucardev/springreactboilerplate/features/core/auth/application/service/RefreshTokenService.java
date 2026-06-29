package com.gucardev.springreactboilerplate.features.core.auth.application.service;

import com.gucardev.springreactboilerplate.features.core.auth.application.exception.AuthExceptionType;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.RefreshTokenUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.LoadRefreshTokenPort;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.LoadUserPort;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.SaveRefreshTokenPort;
import com.gucardev.springreactboilerplate.features.core.auth.domain.model.AuthTokens;
import com.gucardev.springreactboilerplate.features.core.auth.domain.model.RefreshToken;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Exchanges a valid refresh token for a new token bundle, rotating the refresh token: the presented
 * one is revoked and a brand-new one is issued, so a leaked refresh token has a single use.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private final LoadRefreshTokenPort loadRefreshTokenPort;
    private final SaveRefreshTokenPort saveRefreshTokenPort;
    private final LoadUserPort loadUserPort;
    private final AuthTokenService authTokenService;

    @Override
    @Transactional
    public AuthTokens refresh(String refreshTokenValue) {
        RefreshToken refreshToken = loadRefreshTokenPort.findByToken(refreshTokenValue)
                .orElseThrow(AuthExceptionType.INVALID_REFRESH_TOKEN::toException);

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw AuthExceptionType.INVALID_REFRESH_TOKEN.toException();
        }
        if (refreshToken.isExpired()) {
            throw AuthExceptionType.REFRESH_TOKEN_EXPIRED.toException();
        }

        // Rotate: revoke the presented token before minting the replacement.
        refreshToken.revoke();
        saveRefreshTokenPort.save(refreshToken);

        User user = loadUserPort.findByEmail(refreshToken.getUserEmail())
                .orElseThrow(AuthExceptionType.USER_NOT_FOUND::toException);

        return authTokenService.issueTokens(user);
    }
}
