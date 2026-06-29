package com.gucardev.springreactboilerplate.features.core.auth.application.port.in;

/**
 * Input port: purge revoked or expired refresh tokens. Driven by the cleanup scheduler.
 */
public interface CleanupRefreshTokensUseCase {

    /** @return the number of tokens removed. */
    int purgeExpired();
}
