package com.gucardev.springreactboilerplate.features.core.auth.application.service;

import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.CleanupRefreshTokensUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.DeleteExpiredRefreshTokensPort;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Purges revoked or expired refresh tokens via the delete port. Invoked by the cleanup scheduler.
 */
@Service
@RequiredArgsConstructor
public class CleanupRefreshTokensService implements CleanupRefreshTokensUseCase {

    private final DeleteExpiredRefreshTokensPort deleteExpiredRefreshTokensPort;

    @Override
    public int purgeExpired() {
        return deleteExpiredRefreshTokensPort.deleteRevokedOrExpired(LocalDateTime.now());
    }
}
