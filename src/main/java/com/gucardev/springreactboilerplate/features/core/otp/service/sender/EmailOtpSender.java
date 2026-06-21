package com.gucardev.springreactboilerplate.features.core.otp.service.sender;

import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpSendingChannel;
import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpType;
import com.gucardev.springreactboilerplate.infra.config.mail.EmailService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Delivers OTP codes over email via {@link EmailService} (renders {@code templates/email/otp.html}).
 * With no SMTP host configured / {@code app.mail.enabled=false}, EmailService logs instead of sending,
 * so this works out of the box in dev.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailOtpSender implements OtpSender {

    private final EmailService emailService;

    @Override
    public OtpSendingChannel channel() {
        return OtpSendingChannel.EMAIL;
    }

    @Override
    public void send(String destination, String code, OtpType type) {
        emailService.sendHtml(destination, "Your verification code", "otp",
                Map.of("code", code, "type", type.name()));
    }
}
