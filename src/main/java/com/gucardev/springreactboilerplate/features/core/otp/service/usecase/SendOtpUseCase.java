package com.gucardev.springreactboilerplate.features.core.otp.service.usecase;

import com.gucardev.springreactboilerplate.features.core.otp.config.OtpProperties;
import com.gucardev.springreactboilerplate.features.core.otp.entity.Otp;
import com.gucardev.springreactboilerplate.features.core.otp.model.dto.OtpResponseDto;
import com.gucardev.springreactboilerplate.features.core.otp.model.request.SendOtpRequest;
import com.gucardev.springreactboilerplate.features.core.otp.repository.OtpRepository;
import com.gucardev.springreactboilerplate.features.core.otp.service.OtpCodeGenerator;
import com.gucardev.springreactboilerplate.features.core.otp.exception.OtpExceptionType;
import com.gucardev.springreactboilerplate.features.core.otp.service.sender.OtpSenderDispatcher;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Issues a fresh OTP: invalidates any active OTP for the same {@code (destination, type)}, persists
 * the new code, dispatches it over the requested channel, and returns delivery metadata only — the
 * code is never exposed in the response.
 */
@Service
@RequiredArgsConstructor
public class SendOtpUseCase {

    private final OtpRepository otpRepository;
    private final OtpProperties otpProperties;
    private final OtpCodeGenerator codeGenerator;
    private final OtpSenderDispatcher senderDispatcher;

    @Transactional
    public OtpResponseDto execute(SendOtpRequest request) {
        enforceResendCooldown(request);
        otpRepository.invalidateActive(request.getDestination(), request.getType());

        String code = codeGenerator.generate(otpProperties.getLength());
        Otp otp = Otp.builder()
                .destination(request.getDestination())
                .type(request.getType())
                .sendingChannel(request.getSendingChannel())
                .code(code)
                .expiryTime(LocalDateTime.now().plusMinutes(otpProperties.getExpiryMinutes()))
                .used(false)
                .attempts(0)
                .build();
        otpRepository.save(otp);

        senderDispatcher.send(request.getSendingChannel(), request.getDestination(), code, request.getType());

        return OtpResponseDto.builder()
                .destination(otp.getDestination())
                .type(otp.getType())
                .sendingChannel(otp.getSendingChannel())
                .expiryTime(otp.getExpiryTime())
                .build();
    }

    /**
     * Rejects a send that arrives sooner than {@code otp.resend-cooldown-seconds} after the previous
     * one for the same {@code (destination, type)} — throttles SMS/email cost abuse and spam.
     */
    private void enforceResendCooldown(SendOtpRequest request) {
        long cooldown = otpProperties.getResendCooldownSeconds();
        if (cooldown <= 0) {
            return;
        }
        otpRepository.findFirstByDestinationAndTypeOrderByCreatedAtDesc(
                        request.getDestination(), request.getType())
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
