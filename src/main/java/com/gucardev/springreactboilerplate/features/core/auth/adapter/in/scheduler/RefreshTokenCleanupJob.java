package com.gucardev.springreactboilerplate.features.core.auth.adapter.in.scheduler;

import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.CleanupRefreshTokensUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Driving (scheduler) adapter: periodically purges revoked or expired refresh tokens via the
 * cleanup input port. The schedule comes from {@code security.jwt.refresh-token-cleanup-cron}.
 * ShedLock ensures only one instance runs it.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {

    private final CleanupRefreshTokensUseCase cleanupRefreshTokensUseCase;

    @Scheduled(cron = "${security.jwt.refresh-token-cleanup-cron}")
    @SchedulerLock(name = "RefreshTokenCleanupJob_purgeExpired", lockAtMostFor = "PT5M", lockAtLeastFor = "PT10S")
    public void purgeExpiredTokens() {
        int removed = cleanupRefreshTokensUseCase.purgeExpired();
        if (removed > 0) {
            log.info("Refresh-token cleanup removed {} revoked/expired token(s).", removed);
        }
    }
}
