package com.gucardev.springreactboilerplate.features.core.otp.adapter.out.config;

import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.OtpPolicyPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing {@link OtpPolicyPort}: exposes the bound {@link OtpProperties} to the
 * application core, keeping Spring's {@code @ConfigurationProperties} binding out of the core.
 */
@Component
@RequiredArgsConstructor
public class OtpPolicyAdapter implements OtpPolicyPort {

    private final OtpProperties otpProperties;

    @Override
    public int getCodeLength() {
        return otpProperties.getLength();
    }

    @Override
    public long getExpiryMinutes() {
        return otpProperties.getExpiryMinutes();
    }

    @Override
    public int getMaxAttempts() {
        return otpProperties.getMaxAttempts();
    }

    @Override
    public long getResendCooldownSeconds() {
        return otpProperties.getResendCooldownSeconds();
    }
}
