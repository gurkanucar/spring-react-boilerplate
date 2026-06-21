package com.gucardev.springreactboilerplate.infra.config.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.gucardev.springreactboilerplate.infra.config.security.SecurityUtils;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Enforces {@link RateLimited} on annotated bean methods using a per-key resilience4j
 * {@link RateLimiter} held in an in-memory Caffeine cache (idle keys are evicted). Complements the
 * per-IP {@link RateLimitFilter}: this runs inside the request (after auth), so it can key on the
 * authenticated user. Throws a 429 BusinessException that the global handler renders in the standard
 * envelope.
 */
@Aspect
@Component
public class RateLimitedAspect {

    private final Cache<String, RateLimiter> limiters = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(30))
            .maximumSize(100_000)
            .build();

    @Around("@annotation(rateLimited)")
    public Object enforce(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String key = joinPoint.getSignature().toLongString() + "|" + resolvePrincipal(rateLimited.key());
        RateLimiter limiter = limiters.get(key, k -> newLimiter(rateLimited));
        if (!limiter.acquirePermission()) {
            throw RateLimitExceptionType.RATE_LIMIT_EXCEEDED.toException();
        }
        return joinPoint.proceed();
    }

    private RateLimiter newLimiter(RateLimited rateLimited) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(rateLimited.capacity())
                .limitRefreshPeriod(Duration.ofSeconds(rateLimited.refillSeconds()))
                .timeoutDuration(Duration.ZERO) // never block; reject immediately when exhausted
                .build();
        return RateLimiter.of("method-rate-limit", config);
    }

    private String resolvePrincipal(RateLimited.Key key) {
        return switch (key) {
            case USER -> "user:" + SecurityUtils.requireCurrentUserId();
            case IP -> "ip:" + clientIp();
            case USER_OR_IP -> {
                UUID userId = SecurityUtils.currentUserIdOrNull();
                yield userId != null ? "user:" + userId : "ip:" + clientIp();
            }
        };
    }

    private String clientIp() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return "anonymous";
        }
        HttpServletRequest request = attrs.getRequest();
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
