package com.gucardev.springreactboilerplate.features.core.auth.application.port.out;

import com.gucardev.springreactboilerplate.features.core.auth.domain.model.RefreshToken;
import java.util.Optional;

/**
 * Output port: load a refresh token (with its owning user's id/email) by its opaque value.
 */
public interface LoadRefreshTokenPort {

    Optional<RefreshToken> findByToken(String token);
}
