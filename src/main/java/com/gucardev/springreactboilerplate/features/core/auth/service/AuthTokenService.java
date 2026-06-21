package com.gucardev.springreactboilerplate.features.core.auth.service;

import com.gucardev.springreactboilerplate.features.core.auth.entity.RefreshToken;
import com.gucardev.springreactboilerplate.features.core.auth.model.dto.TokenResponseDto;
import com.gucardev.springreactboilerplate.features.core.auth.repository.RefreshTokenRepository;
import com.gucardev.springreactboilerplate.features.core.user.entity.User;
import com.gucardev.springreactboilerplate.features.core.user.mapper.UserMapper;
import com.gucardev.springreactboilerplate.infra.config.security.jwt.JwtProperties;
import com.gucardev.springreactboilerplate.infra.config.security.jwt.JwtService;
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

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final UserMapper userMapper;

    @Transactional
    public TokenResponseDto issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType(BEARER)
                .expiresIn(jwtService.getAccessTokenValiditySeconds())
                .user(userMapper.toDto(user))
                .build();
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(jwtProperties.getRefreshTokenValidityInMinutes()))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }
}
