package com.gucardev.springreactboilerplate.domain.otp.scheduler;

import com.gucardev.springreactboilerplate.domain.otp.repository.OtpRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodically purges expired or already-used OTPs. The schedule comes from {@code otp.cleanup-cron};
 * ShedLock ensures only one instance runs it.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OtpCleanupJob {

    private final OtpRepository otpRepository;

    @Scheduled(cron = "${otp.cleanup-cron}")
    @SchedulerLock(name = "OtpCleanupJob_purge", lockAtMostFor = "PT5M", lockAtLeastFor = "PT10S")
    public void purgeExpiredOtps() {
        int removed = otpRepository.deleteExpiredOrUsed(LocalDateTime.now());
        if (removed > 0) {
            log.info("OTP cleanup removed {} expired/used OTP(s).", removed);
        }
    }
}
