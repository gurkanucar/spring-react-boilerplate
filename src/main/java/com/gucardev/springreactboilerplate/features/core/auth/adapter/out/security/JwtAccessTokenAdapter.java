package com.gucardev.springreactboilerplate.features.core.auth.adapter.out.security;

import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.AccessTokenPort;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import com.gucardev.springreactboilerplate.infra.config.security.jwt.JwtProperties;
import com.gucardev.springreactboilerplate.infra.config.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing {@link AccessTokenPort} by delegating to the infrastructure JWT service and
 * properties. Confines those infra dependencies to a single adapter.
 */
@Component
@RequiredArgsConstructor
public class JwtAccessTokenAdapter implements AccessTokenPort {

    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Override
    public String generateAccessToken(User user) {
        return jwtService.generateAccessToken(user);
    }

    @Override
    public long getAccessTokenValiditySeconds() {
        return jwtService.getAccessTokenValiditySeconds();
    }

    @Override
    public long getRefreshTokenValidityMinutes() {
        return jwtProperties.getRefreshTokenValidityInMinutes();
    }
}
