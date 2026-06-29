package com.gucardev.springreactboilerplate.features.core.auth.application.port.out;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;

/**
 * Output port: mint JWT access tokens and expose the configured token lifetimes. Backed by an
 * adapter delegating to the infrastructure JWT service, keeping the auth core off that infra.
 */
public interface AccessTokenPort {

    String generateAccessToken(User user);

    long getAccessTokenValiditySeconds();

    long getRefreshTokenValidityMinutes();
}
