package com.gucardev.springreactboilerplate.features.core.otp.application.port.out;

import java.time.LocalDateTime;

/**
 * Output port: purge expired or already-used OTPs from the store.
 */
public interface PurgeExpiredOtpPort {

    /** @return the number of rows removed. */
    int deleteExpiredOrUsed(LocalDateTime now);
}
