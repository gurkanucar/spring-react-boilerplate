package com.gucardev.springreactboilerplate.features.core.otp.application.service;

import com.gucardev.springreactboilerplate.features.core.otp.application.exception.OtpExceptionType;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.in.SendOtpCommand;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.in.SendOtpUseCase;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.GenerateOtpCodePort;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.InvalidateActiveOtpPort;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.LoadOtpPort;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.OtpPolicyPort;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.SaveOtpPort;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.SendOtpNotificationPort;
import com.gucardev.springreactboilerplate.features.core.otp.domain.model.Otp;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Issues a fresh OTP: invalidates any active OTP for the same {@code (destination, type)}, persists
 * the new code, dispatches it over the requested channel, and returns the persisted domain model — the
 * code is never exposed in the response.
 */
@Service
@RequiredArgsConstructor
public class SendOtpService implements SendOtpUseCase {

    private final LoadOtpPort loadOtpPort;
    private final SaveOtpPort saveOtpPort;
    private final InvalidateActiveOtpPort invalidateActiveOtpPort;
    private final GenerateOtpCodePort generateOtpCodePort;
    private final SendOtpNotificationPort sendOtpNotificationPort;
    private final OtpPolicyPort otpPolicy;

    @Override
    @Transactional
    public Otp send(SendOtpCommand command) {
        enforceResendCooldown(command);
        invalidateActiveOtpPort.invalidateActive(command.destination(), command.type());

        String code = generateOtpCodePort.generate(otpPolicy.getCodeLength());
        Otp otp = saveOtpPort.save(Otp.builder()
                .destination(command.destination())
                .type(command.type())
                .sendingChannel(command.sendingChannel())
                .code(code)
                .expiryTime(LocalDateTime.now().plusMinutes(otpPolicy.getExpiryMinutes()))
                .used(false)
                .attempts(0)
                .build());

        sendOtpNotificationPort.send(otp);

        return otp;
    }

    /**
     * Rejects a send that arrives sooner than {@code otp.resend-cooldown-seconds} after the previous
     * one for the same {@code (destination, type)} — throttles SMS/email cost abuse and spam.
     */
    private void enforceResendCooldown(SendOtpCommand command) {
        long cooldown = otpPolicy.getResendCooldownSeconds();
        if (cooldown <= 0) {
            return;
        }
        loadOtpPort.findLatestByDestinationAndType(command.destination(), command.type())
                .ifPresent(last -> {
                    LocalDateTime nextAllowed = last.getCreatedAt().plusSeconds(cooldown);
                    LocalDateTime now = LocalDateTime.now();
                    if (nextAllowed.isAfter(now)) {
                        long secondsLeft = Duration.between(now, nextAllowed).toSeconds() + 1;
                        throw OtpExceptionType.RESEND_TOO_SOON.toException(secondsLeft);
                    }
                });
    }
}
