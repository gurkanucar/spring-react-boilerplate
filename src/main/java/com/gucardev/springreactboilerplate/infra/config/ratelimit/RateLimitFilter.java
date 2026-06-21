package com.gucardev.springreactboilerplate.infra.config.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.gucardev.springreactboilerplate.infra.config.message.MessageUtil;
import com.gucardev.springreactboilerplate.infra.exception.model.ApiError;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

/**
 * Per-IP token-bucket rate limiter (bucket4j) for sensitive endpoints (login, register, OTP send),
 * so credential stuffing / OTP abuse is throttled before reaching the controllers. Over-limit
 * requests get a {@code 429} in the standard {@link ApiError} envelope plus a {@code Retry-After}
 * header. Buckets are held in an in-memory Caffeine cache (per node); see {@link RateLimitProperties}
 * for tuning and {@code app.rate-limit.enabled} to switch it off.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(10))
            .maximumSize(100_000)
            .build();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (!properties.isEnabled() || !isLimited(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String key = clientIp(request) + "|" + request.getRequestURI();
        Bucket bucket = buckets.get(key, k -> newBucket());
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            writeTooManyRequests(response);
        }
    }

    private boolean isLimited(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return properties.getPaths().stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(properties.getCapacity())
                .refillGreedy(properties.getCapacity(), Duration.ofSeconds(properties.getRefillSeconds()))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeTooManyRequests(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Retry-After", String.valueOf(properties.getRefillSeconds()));
        ApiError error = ApiError.business(429,
                MessageUtil.getMessage("error.rate_limit_exceeded"),
                "RATE_LIMIT_EXCEEDED", MDC.get("traceId"));
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
