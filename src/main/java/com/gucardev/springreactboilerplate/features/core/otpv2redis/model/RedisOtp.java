package com.gucardev.springreactboilerplate.features.core.otpv2redis.model;

import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpSendingChannel;
import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpType;
import java.time.LocalDateTime;

/**
 * An OTP held in Redis instead of a relational row. There is no {@code used} flag and no cleanup
 * job: the Redis key carries a TTL equal to the validity window, so the entry simply disappears
 * when it expires — a missed {@code GET} <em>is</em> the "expired/invalid" signal. A successful
 * verification deletes the key (single use). State is shared across instances by Redis itself, so
 * this works unchanged in a multi-instance deployment.
 *
 * @param expiresAt the wall-clock expiry, mirrored from the key TTL so it can be echoed back in the
 *                  response (the TTL remains the source of truth for invalidation).
 */
public record RedisOtp(
        String code,
        String destination,
        OtpType type,
        OtpSendingChannel channel,
        LocalDateTime expiresAt) {
}
