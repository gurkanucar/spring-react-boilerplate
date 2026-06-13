package com.gucardev.springreactboilerplate.domain.auth.scheduler;

import com.gucardev.springreactboilerplate.domain.auth.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodically purges revoked or expired refresh tokens. The schedule comes from
 * {@code security.jwt.refresh-token-cleanup-cron}. ShedLock ensures only one instance runs it.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "${security.jwt.refresh-token-cleanup-cron}")
    @SchedulerLock(name = "RefreshTokenCleanupJob_purgeExpired", lockAtMostFor = "PT5M", lockAtLeastFor = "PT10S")
    public void purgeExpiredTokens() {
        int removed = refreshTokenRepository.deleteRevokedOrExpired(LocalDateTime.now());
        if (removed > 0) {
            log.info("Refresh-token cleanup removed {} revoked/expired token(s).", removed);
        }
    }
}
