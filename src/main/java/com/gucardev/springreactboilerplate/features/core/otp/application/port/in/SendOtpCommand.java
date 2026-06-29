package com.gucardev.springreactboilerplate.features.core.otp.application.port.in;

import com.gucardev.springreactboilerplate.features.core.otp.domain.model.OtpSendingChannel;
import com.gucardev.springreactboilerplate.features.core.otp.domain.model.OtpType;

/**
 * Driving-side command for issuing a new OTP. Carries already-validated input from a driving adapter
 * into the application core, decoupling the core from any particular transport.
 */
public record SendOtpCommand(
        String destination,
        OtpType type,
        OtpSendingChannel sendingChannel
) {
}
