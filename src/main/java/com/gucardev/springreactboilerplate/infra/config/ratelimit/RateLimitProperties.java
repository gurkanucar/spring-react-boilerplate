package com.gucardev.springreactboilerplate.infra.config.ratelimit;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Tunables for {@link RateLimitFilter}. A token bucket of {@code capacity} tokens that refills fully
 * every {@code refillSeconds}, applied per client IP to each configured path pattern.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    /** Master switch. */
    private boolean enabled = true;

    /** Max requests allowed within one refill window, per IP per matched path. Kept generous
     * because many users may share one public IP (NAT); per-user limits use {@code @RateLimited}. */
    private int capacity = 100;

    /** Window length in seconds after which the bucket refills to {@link #capacity}. */
    private int refillSeconds = 60;

    /** Ant-style path patterns the limiter applies to (sensitive auth endpoints by default). */
    private List<String> paths = List.of("/auth/login", "/auth/register", "/otp/send");
}
