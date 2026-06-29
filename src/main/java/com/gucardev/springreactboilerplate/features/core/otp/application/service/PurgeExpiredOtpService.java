package com.gucardev.springreactboilerplate.features.core.otp.application.service;

import com.gucardev.springreactboilerplate.features.core.otp.application.port.in.PurgeExpiredOtpUseCase;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.PurgeExpiredOtpPort;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Purges expired or already-used OTPs. Driven by the scheduled cleanup adapter.
 */
@Service
@RequiredArgsConstructor
public class PurgeExpiredOtpService implements PurgeExpiredOtpUseCase {

    private final PurgeExpiredOtpPort purgeExpiredOtpPort;

    @Override
    @Transactional
    public int purgeExpired() {
        return purgeExpiredOtpPort.deleteExpiredOrUsed(LocalDateTime.now());
    }
}
