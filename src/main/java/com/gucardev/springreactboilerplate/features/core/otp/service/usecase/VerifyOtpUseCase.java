package com.gucardev.springreactboilerplate.features.core.otp.service.usecase;

import com.gucardev.springreactboilerplate.features.core.otp.entity.Otp;
import com.gucardev.springreactboilerplate.features.core.otp.exception.OtpExceptionType;
import com.gucardev.springreactboilerplate.features.core.otp.model.request.VerifyOtpRequest;
import com.gucardev.springreactboilerplate.features.core.otp.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verifies a submitted code against the active OTP for {@code (destination, type)}. A correct code
 * burns the OTP (single use); a wrong code is rejected without burning it, so the caller can retry
 * until the OTP expires or is replaced. Brute-force abuse is throttled by the per-IP rate limit on
 * the endpoint rather than a per-OTP attempt counter.
 */
@Service
@RequiredArgsConstructor
public class VerifyOtpUseCase {

    private final OtpRepository otpRepository;

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

        if (!otp.getCode().equals(request.getOtp())) {
            throw OtpExceptionType.INVALID_CODE.toException();
        }

        otp.setUsed(true);
        otpRepository.save(otp);
    }
}
