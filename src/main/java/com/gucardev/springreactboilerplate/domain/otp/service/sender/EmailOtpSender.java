package com.gucardev.springreactboilerplate.domain.otp.service.sender;

import com.gucardev.springreactboilerplate.domain.otp.enums.OtpSendingChannel;
import com.gucardev.springreactboilerplate.domain.otp.enums.OtpType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Placeholder email sender: logs the code instead of sending mail. Replace with a real
 * integration (e.g. SES/SMTP) — keep the {@link #channel()} as {@code EMAIL}.
 */
@Slf4j
@Component
public class EmailOtpSender implements OtpSender {

    @Override
    public OtpSendingChannel channel() {
        return OtpSendingChannel.EMAIL;
    }

    @Override
    public void send(String destination, String code, OtpType type) {
        log.info("[OTP/EMAIL] type={} to={} code={} (stub sender — wire a real email provider here)",
                type, destination, code);
    }
}
