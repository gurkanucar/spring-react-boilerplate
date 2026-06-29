package com.gucardev.springreactboilerplate.features.core.otp.adapter.in.scheduler;

import com.gucardev.springreactboilerplate.features.core.otp.application.port.in.PurgeExpiredOtpUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Driving (scheduler) adapter that periodically purges expired or already-used OTPs. The schedule
 * comes from {@code otp.cleanup-cron}; ShedLock ensures only one instance runs it. It only talks to
 * the input port.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OtpCleanupJob {

    private final PurgeExpiredOtpUseCase purgeExpiredOtpUseCase;

    @Scheduled(cron = "${otp.cleanup-cron}")
    @SchedulerLock(name = "OtpCleanupJob_purge", lockAtMostFor = "PT5M", lockAtLeastFor = "PT10S")
    public void purgeExpiredOtps() {
        int removed = purgeExpiredOtpUseCase.purgeExpired();
        if (removed > 0) {
            log.info("OTP cleanup removed {} expired/used OTP(s).", removed);
        }
    }
}
