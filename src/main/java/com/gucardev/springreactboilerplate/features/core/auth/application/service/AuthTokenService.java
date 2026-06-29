package com.gucardev.springreactboilerplate.features.core.auth.application.service;

import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.AccessTokenPort;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.SaveRefreshTokenPort;
import com.gucardev.springreactboilerplate.features.core.auth.domain.model.AuthTokens;
import com.gucardev.springreactboilerplate.features.core.auth.domain.model.RefreshToken;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Issues credential bundles: a freshly minted JWT access token plus a newly persisted refresh
 * token. Shared by registration, login and refresh so the response shape is identical everywhere.
 */
@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private static final String BEARER = "Bearer";

    private final AccessTokenPort accessTokenPort;
    private final SaveRefreshTokenPort saveRefreshTokenPort;

    @Transactional
    public AuthTokens issueTokens(User user) {
        String accessToken = accessTokenPort.generateAccessToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return AuthTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType(BEARER)
                .expiresIn(accessTokenPort.getAccessTokenValiditySeconds())
                .user(user)
                .build();
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(user.getId())
                .userEmail(user.getEmail())
                .expiresAt(LocalDateTime.now().plusMinutes(accessTokenPort.getRefreshTokenValidityMinutes()))
                .revoked(false)
                .build();
        return saveRefreshTokenPort.save(refreshToken);
    }
}
