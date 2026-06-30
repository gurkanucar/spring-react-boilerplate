package com.gucardev.springreactboilerplate.features.core.otpv2redis.service;

import com.gucardev.springreactboilerplate.features.core.otp.config.OtpProperties;
import com.gucardev.springreactboilerplate.features.core.otp.exception.OtpExceptionType;
import com.gucardev.springreactboilerplate.features.core.otp.model.dto.OtpResponseDto;
import com.gucardev.springreactboilerplate.features.core.otp.model.request.SendOtpRequest;
import com.gucardev.springreactboilerplate.features.core.otp.service.OtpCodeGenerator;
import com.gucardev.springreactboilerplate.features.core.otp.service.sender.OtpSenderDispatcher;
import com.gucardev.springreactboilerplate.features.core.otpv2redis.model.RedisOtp;
import com.gucardev.springreactboilerplate.features.core.otpv2redis.store.RedisOtpStore;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Redis-backed counterpart of {@code SendOtpUseCase}. Issues a fresh OTP into Redis with a TTL equal
 * to the validity window, dispatches it over the requested channel, and returns delivery metadata
 * only. Reuses the same code generator, sender dispatcher and {@code otp.*} properties as the DB
 * variant — only the storage layer differs.
 *
 * <p>There is no {@code @Transactional}: Redis is the single source of truth and every write is its
 * own atomic command, which is what keeps this correct across multiple instances. Writing the OTP
 * key for the triple inherently invalidates any previous one (overwrite), so no explicit
 * "invalidate active" step is needed.
 */
@Service
@RequiredArgsConstructor
public class SendOtpRedisUseCase {

    private final RedisOtpStore store;
    private final OtpProperties otpProperties;
    private final OtpCodeGenerator codeGenerator;
    private final OtpSenderDispatcher senderDispatcher;

    public OtpResponseDto execute(SendOtpRequest request) {
        enforceResendCooldown(request);

        String code = codeGenerator.generate(otpProperties.getLength());
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpProperties.getExpiryMinutes());

        store.save(
                new RedisOtp(code, request.getDestination(), request.getType(),
                        request.getSendingChannel(), expiresAt),
                Duration.ofMinutes(otpProperties.getExpiryMinutes()));

        long cooldown = otpProperties.getResendCooldownSeconds();
        if (cooldown > 0) {
            store.startCooldown(request.getType(), request.getSendingChannel(),
                    request.getDestination(), Duration.ofSeconds(cooldown));
        }

        senderDispatcher.send(request.getSendingChannel(), request.getDestination(), code, request.getType());

        return OtpResponseDto.builder()
                .destination(request.getDestination())
                .type(request.getType())
                .sendingChannel(request.getSendingChannel())
                .expiryTime(expiresAt)
                .build();
    }

    /**
     * Rejects a send that arrives while the cooldown marker for the same triple is still alive —
     * the marker's remaining TTL is the number of seconds the caller must wait.
     */
    private void enforceResendCooldown(SendOtpRequest request) {
        if (otpProperties.getResendCooldownSeconds() <= 0) {
            return;
        }
        store.cooldownSecondsLeft(request.getType(), request.getSendingChannel(), request.getDestination())
                .ifPresent(secondsLeft -> {
                    throw OtpExceptionType.RESEND_TOO_SOON.toException(secondsLeft);
                });
    }
}
