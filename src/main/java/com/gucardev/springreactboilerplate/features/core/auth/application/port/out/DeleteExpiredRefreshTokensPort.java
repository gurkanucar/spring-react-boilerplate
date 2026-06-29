package com.gucardev.springreactboilerplate.features.core.auth.application.port.out;

import java.time.LocalDateTime;

/**
 * Output port: bulk-purge revoked or expired refresh tokens.
 */
public interface DeleteExpiredRefreshTokensPort {

    /** @return the number of rows removed. */
    int deleteRevokedOrExpired(LocalDateTime now);
}
