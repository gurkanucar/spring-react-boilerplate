package com.gucardev.springreactboilerplate.features.core.auth.domain.model;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import lombok.Builder;
import lombok.Getter;

/**
 * An issued credential bundle — the pure domain model returned by the login/register/refresh use
 * cases: a freshly minted JWT access token, a newly persisted refresh token, and the authenticated
 * user (a domain {@link User}). The web adapter maps it to the transport DTO.
 */
@Getter
@Builder
public class AuthTokens {

    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final Long expiresIn;
    private final User user;
}
