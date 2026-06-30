package com.gucardev.springreactboilerplate.features.core.otpv2redis.service;

import com.gucardev.springreactboilerplate.features.core.otp.exception.OtpExceptionType;
import com.gucardev.springreactboilerplate.features.core.otpv2redis.model.RedisOtp;
import com.gucardev.springreactboilerplate.features.core.otpv2redis.model.request.VerifyOtpRedisRequest;
import com.gucardev.springreactboilerplate.features.core.otpv2redis.store.RedisOtpStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Redis-backed counterpart of {@code VerifyOtpUseCase}. Loads the active OTP for the
 * {@code (type, channel, destination)} triple from Redis and compares the submitted code. A missing
 * key means the OTP has already expired (TTL elapsed) or was never issued, surfaced as
 * {@code NO_ACTIVE_OTP}. A correct code burns the OTP (single use); a wrong code is simply rejected,
 * with no attempt tracking — abuse is throttled by the per-IP rate limit on the endpoint instead.
 */
@Service
@RequiredArgsConstructor
public class VerifyOtpRedisUseCase {

    private final RedisOtpStore store;

    public void execute(VerifyOtpRedisRequest request) {
        RedisOtp otp = store
                .find(request.getType(), request.getSendingChannel(), request.getDestination())
                .orElseThrow(OtpExceptionType.NO_ACTIVE_OTP::toException);

        if (!otp.code().equals(request.getOtp())) {
            throw OtpExceptionType.INVALID_CODE.toException();
        }

        store.delete(request.getType(), request.getSendingChannel(), request.getDestination());
    }
}
