package com.gucardev.springreactboilerplate.features.core.auth.application.port.out;

import com.gucardev.springreactboilerplate.features.core.auth.domain.model.RefreshToken;

/**
 * Output port: persist a refresh token (insert or update) and return the stored state.
 */
public interface SaveRefreshTokenPort {

    RefreshToken save(RefreshToken refreshToken);
}
