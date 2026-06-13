package com.gucardev.springreactboilerplate.domain.auth.service.usecase;

import com.gucardev.springreactboilerplate.domain.auth.entity.RefreshToken;
import com.gucardev.springreactboilerplate.domain.auth.exception.AuthExceptionType;
import com.gucardev.springreactboilerplate.domain.auth.model.dto.TokenResponseDto;
import com.gucardev.springreactboilerplate.domain.auth.model.request.RefreshTokenRequest;
import com.gucardev.springreactboilerplate.domain.auth.repository.RefreshTokenRepository;
import com.gucardev.springreactboilerplate.domain.auth.service.AuthTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Exchanges a valid refresh token for a new token bundle, rotating the refresh token: the presented
 * one is revoked and a brand-new one is issued, so a leaked refresh token has a single use.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthTokenService authTokenService;

    @Transactional
    public TokenResponseDto execute(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> AuthExceptionType.INVALID_REFRESH_TOKEN.toException());

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw AuthExceptionType.INVALID_REFRESH_TOKEN.toException();
        }
        if (refreshToken.isExpired()) {
            throw AuthExceptionType.REFRESH_TOKEN_EXPIRED.toException();
        }

        // Rotate: revoke the presented token before minting the replacement.
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return authTokenService.issueTokens(refreshToken.getUser());
    }
}
