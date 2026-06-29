package com.gucardev.springreactboilerplate.features.core.otp.application.port.in;

import com.gucardev.springreactboilerplate.features.core.otp.domain.model.OtpType;

/**
 * Driving-side command for verifying a submitted OTP code.
 */
public record VerifyOtpCommand(
        String destination,
        OtpType type,
        String otp
) {
}
