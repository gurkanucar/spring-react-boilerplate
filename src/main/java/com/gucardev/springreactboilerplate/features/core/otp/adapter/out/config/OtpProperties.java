package com.gucardev.springreactboilerplate.features.core.otp.adapter.out.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds the {@code otp.*} block from application.yml.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "otp")
public class OtpProperties {

    /** Number of digits in a generated code. */
    private int length = 6;

    /** How long a code stays valid, in minutes. */
    private long expiryMinutes = 5;

    /** Max verification attempts before the code is locked. */
    private int maxAttempts = 5;

    /** Minimum seconds between two sends for the same (destination, type). 0 disables the cooldown. */
    private long resendCooldownSeconds = 60;

    /** Spring cron expression for purging expired/used OTPs. */
    private String cleanupCron = "0 0 * * * *";
}
