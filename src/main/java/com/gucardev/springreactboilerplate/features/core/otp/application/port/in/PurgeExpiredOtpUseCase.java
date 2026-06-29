package com.gucardev.springreactboilerplate.features.core.otp.application.port.in;

/**
 * Input port: purge expired or already-used OTPs. Driven by the scheduled cleanup adapter.
 */
public interface PurgeExpiredOtpUseCase {

    /** @return the number of OTP rows removed. */
    int purgeExpired();
}
