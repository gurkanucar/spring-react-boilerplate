package com.gucardev.springreactboilerplate.features.core.otp.service.usecase;

import com.gucardev.springreactboilerplate.features.core.otp.config.OtpProperties;
import com.gucardev.springreactboilerplate.features.core.otp.entity.Otp;
import com.gucardev.springreactboilerplate.features.core.otp.exception.OtpExceptionType;
import com.gucardev.springreactboilerplate.features.core.otp.model.request.VerifyOtpRequest;
import com.gucardev.springreactboilerplate.features.core.otp.repository.OtpRepository;
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
public class VerifyOtpUseCase {

    private final OtpRepository otpRepository;
    private final OtpProperties otpProperties;

    @Transactional
    public void execute(VerifyOtpRequest request) {
        Otp otp = otpRepository
                .findFirstByDestinationAndTypeAndUsedFalseOrderByCreatedAtDesc(
                        request.getDestination(), request.getType())
                .orElseThrow(OtpExceptionType.NO_ACTIVE_OTP::toException);

        if (otp.isExpired()) {
            otp.setUsed(true);
            otpRepository.save(otp);
            throw OtpExceptionType.EXPIRED.toException();
        }

        // Locked out: the code stays active (so a new send can invalidate it) but no longer accepts
        // guesses until it expires / is replaced.
        if (otp.getAttempts() >= otpProperties.getMaxAttempts()) {
            throw OtpExceptionType.MAX_ATTEMPTS_EXCEEDED.toException();
        }

        if (!otp.getCode().equals(request.getOtp())) {
            otp.setAttempts(otp.getAttempts() + 1);
            otpRepository.save(otp);
            throw OtpExceptionType.INVALID_CODE.toException();
        }

        otp.setUsed(true);
        otpRepository.save(otp);
    }
}
