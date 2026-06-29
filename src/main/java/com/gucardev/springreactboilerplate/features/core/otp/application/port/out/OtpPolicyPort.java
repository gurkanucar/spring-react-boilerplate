package com.gucardev.springreactboilerplate.features.core.otp.application.port.out;

/**
 * Output port exposing the configurable OTP policy to the application core, so the core depends on a
 * port rather than on Spring's {@code @ConfigurationProperties} binding.
 */
public interface OtpPolicyPort {

    /** Number of digits in a generated code. */
    int getCodeLength();

    /** How long a code stays valid, in minutes. */
    long getExpiryMinutes();

    /** Max verification attempts before the code is locked. */
    int getMaxAttempts();

    /** Minimum seconds between two sends for the same (destination, type). 0 disables the cooldown. */
    long getResendCooldownSeconds();
}
