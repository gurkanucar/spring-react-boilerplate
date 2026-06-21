package com.gucardev.springreactboilerplate.infra.config.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import java.time.Duration;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Programmatic per-key rate limiting on top of resilience4j's {@link RateLimiter} — for the per-user
 * (or any dynamic key) case the built-in name-based {@code @RateLimiter} annotation can't express.
 * Call it explicitly from a service method; no custom annotation involved.
 *
 * <pre>{@code
 * keyedRateLimiter.acquireForUser("createNews", userId, 20, 60); // 20 / minute per user
 * }</pre>
 *
 * Over-limit calls throw resilience4j's {@link RequestNotPermitted}, which the global handler renders
 * as 429. One {@link RateLimiter} is kept per key in an idle-evicting Caffeine cache (per instance).
 */
@Component
public class KeyedRateLimiter {

    private final Cache<String, RateLimiter> limiters = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(30))
            .maximumSize(100_000)
            .build();

    /** Limit {@code limitForPeriod} calls per {@code refreshSeconds} for {@code (name, userId)}. */
    public void acquireForUser(String name, UUID userId, int limitForPeriod, int refreshSeconds) {
        acquire(name + ":user:" + userId, limitForPeriod, refreshSeconds);
    }

    /** Limit by an arbitrary key (e.g. an IP or tenant id). */
    public void acquire(String key, int limitForPeriod, int refreshSeconds) {
        RateLimiter limiter = limiters.get(key, k -> RateLimiter.of(k, RateLimiterConfig.custom()
                .limitForPeriod(limitForPeriod)
                .limitRefreshPeriod(Duration.ofSeconds(refreshSeconds))
                .timeoutDuration(Duration.ZERO) // reject immediately when exhausted
                .build()));
        if (!limiter.acquirePermission()) {
            throw RequestNotPermitted.createRequestNotPermitted(limiter);
        }
    }
}
