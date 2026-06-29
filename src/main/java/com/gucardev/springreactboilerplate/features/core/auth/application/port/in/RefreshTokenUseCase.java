package com.gucardev.springreactboilerplate.features.core.auth.application.port.in;

import com.gucardev.springreactboilerplate.features.core.auth.domain.model.AuthTokens;

/**
 * Input port: exchange a refresh token for a new (rotated) token bundle.
 */
public interface RefreshTokenUseCase {

    AuthTokens refresh(String refreshToken);
}
