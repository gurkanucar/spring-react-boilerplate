package com.gucardev.springreactboilerplate.features.core.otpv2redis.store;

import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpSendingChannel;
import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpType;
import com.gucardev.springreactboilerplate.features.core.otpv2redis.model.RedisOtp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis-backed storage for OTP v2. Each OTP is one Redis hash whose key encodes the
 * {@code (type, channel, destination)} triple, so at most one OTP is active per triple — issuing a
 * new one for the same triple simply overwrites the previous hash. The key is given a TTL equal to
 * the validity window: when it elapses the key vanishes, which is exactly what makes the code
 * invalid (there is nothing to {@code GET}). No {@code used} flag and no cleanup job are needed; a
 * successful verification just deletes the key.
 *
 * <p>The resend cooldown is a separate short-lived marker key (presence = "too soon"), again
 * self-expiring via TTL. Every key is prefixed with {@code app.redis.key-prefix} to share the same
 * namespace as the rest of the app on a shared Redis.
 */
@Component
@RequiredArgsConstructor
public class RedisOtpStore {

    /** Hash key namespace for the OTP itself. */
    private static final String NS = "otpv2";
    /** Marker key namespace for the resend cooldown. */
    private static final String NS_COOLDOWN = "otpv2:cd";

    private static final String F_CODE = "code";
    private static final String F_DESTINATION = "destination";
    private static final String F_TYPE = "type";
    private static final String F_CHANNEL = "channel";
    private static final String F_EXPIRES_AT = "expiresAt";

    private final StringRedisTemplate redis;

    @Value("${app.redis.key-prefix:}")
    private String keyPrefix;

    /**
     * Persists (or overwrites) the active OTP for its triple and (re)sets the key TTL to {@code ttl}.
     * Because the field set is identical on every write, an overwrite fully replaces the previous OTP.
     */
    public void save(RedisOtp otp, Duration ttl) {
        String key = otpKey(otp.type(), otp.channel(), otp.destination());
        Map<String, String> fields = new HashMap<>();
        fields.put(F_CODE, otp.code());
        fields.put(F_DESTINATION, otp.destination());
        fields.put(F_TYPE, otp.type().name());
        fields.put(F_CHANNEL, otp.channel().name());
        fields.put(F_EXPIRES_AT, otp.expiresAt().toString());
        redis.opsForHash().putAll(key, fields);
        redis.expire(key, ttl);
    }

    /** The active OTP for the triple, or empty when the key has expired / never existed. */
    public Optional<RedisOtp> find(OtpType type, OtpSendingChannel channel, String destination) {
        Map<Object, Object> fields = redis.opsForHash().entries(otpKey(type, channel, destination));
        if (fields.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new RedisOtp(
                (String) fields.get(F_CODE),
                destination,
                type,
                channel,
                LocalDateTime.parse((String) fields.get(F_EXPIRES_AT))));
    }

    /** Burns the OTP (single use) — called on a successful verification. */
    public void delete(OtpType type, OtpSendingChannel channel, String destination) {
        redis.delete(otpKey(type, channel, destination));
    }

    /** Seconds left on the cooldown marker for the triple, or empty when a resend is allowed. */
    public Optional<Long> cooldownSecondsLeft(OtpType type, OtpSendingChannel channel, String destination) {
        Long ttl = redis.getExpire(cooldownKey(type, channel, destination), TimeUnit.SECONDS);
        return ttl != null && ttl > 0 ? Optional.of(ttl) : Optional.empty();
    }

    /** Starts the resend cooldown for the triple (self-expiring marker). */
    public void startCooldown(OtpType type, OtpSendingChannel channel, String destination, Duration ttl) {
        redis.opsForValue().set(cooldownKey(type, channel, destination), "1", ttl);
    }

    private String otpKey(OtpType type, OtpSendingChannel channel, String destination) {
        return keyPrefix + NS + ":" + type.name() + ":" + channel.name() + ":" + destination;
    }

    private String cooldownKey(OtpType type, OtpSendingChannel channel, String destination) {
        return keyPrefix + NS_COOLDOWN + ":" + type.name() + ":" + channel.name() + ":" + destination;
    }
}
