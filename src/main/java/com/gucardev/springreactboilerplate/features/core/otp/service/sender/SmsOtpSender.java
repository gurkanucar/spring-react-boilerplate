package com.gucardev.springreactboilerplate.features.core.otp.service.sender;

import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpSendingChannel;
import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Placeholder SMS sender: logs the code instead of calling a provider. Replace with a real
 * integration (e.g. Twilio) — keep the {@link #channel()} as {@code SMS}.
 */
@Slf4j
@Component
public class SmsOtpSender implements OtpSender {

    @Override
    public OtpSendingChannel channel() {
        return OtpSendingChannel.SMS;
    }

    @Override
    public void send(String destination, String code, OtpType type) {
        log.info("[OTP/SMS] type={} to={} code={} (stub sender — wire a real SMS provider here)",
                type, destination, code);
    }
}
