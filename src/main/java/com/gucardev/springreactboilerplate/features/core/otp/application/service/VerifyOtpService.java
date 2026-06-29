package com.gucardev.springreactboilerplate.features.core.otp.application.service;

import com.gucardev.springreactboilerplate.features.core.otp.application.exception.OtpExceptionType;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.in.VerifyOtpCommand;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.in.VerifyOtpUseCase;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.LoadOtpPort;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.OtpPolicyPort;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.SaveOtpPort;
import com.gucardev.springreactboilerplate.features.core.otp.domain.model.Otp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verifies a submitted code against the active OTP for {@code (destination, type)}. A correct code
 * burns the OTP (single use). A wrong code increments the attempt counter and burns the OTP once
 * the max-attempts threshold is reached, forcing the caller to request a new one.
 */
@Service
@RequiredArgsConstructor
public class VerifyOtpService implements VerifyOtpUseCase {

    private final LoadOtpPort loadOtpPort;
    private final SaveOtpPort saveOtpPort;
    private final OtpPolicyPort otpPolicy;

    @Override
    @Transactional
    public void verify(VerifyOtpCommand command) {
        Otp otp = loadOtpPort
                .findActiveByDestinationAndType(command.destination(), command.type())
                .orElseThrow(OtpExceptionType.NO_ACTIVE_OTP::toException);

        if (otp.isExpired()) {
            otp.markUsed();
            saveOtpPort.save(otp);
            throw OtpExceptionType.EXPIRED.toException();
        }

        // Locked out: the code stays active (so a new send can invalidate it) but no longer accepts
        // guesses until it expires / is replaced.
        if (otp.hasReachedMaxAttempts(otpPolicy.getMaxAttempts())) {
            throw OtpExceptionType.MAX_ATTEMPTS_EXCEEDED.toException();
        }

        if (!otp.matches(command.otp())) {
            otp.incrementAttempts();
            saveOtpPort.save(otp);
            throw OtpExceptionType.INVALID_CODE.toException();
        }

        otp.markUsed();
        saveOtpPort.save(otp);
    }
}
