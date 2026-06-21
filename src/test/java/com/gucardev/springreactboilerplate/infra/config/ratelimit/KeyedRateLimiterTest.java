package com.gucardev.springreactboilerplate.infra.config.ratelimit;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class KeyedRateLimiterTest {

    private final KeyedRateLimiter limiter = new KeyedRateLimiter();

    @Test
    void thirdCallBySameUser_isRejected() {
        UUID user = UUID.randomUUID();

        limiter.acquireForUser("createNews", user, 2, 60);
        limiter.acquireForUser("createNews", user, 2, 60);

        assertThatThrownBy(() -> limiter.acquireForUser("createNews", user, 2, 60))
                .isInstanceOf(RequestNotPermitted.class);
    }

    @Test
    void limitIsPerUser_notShared() {
        UUID userA = UUID.randomUUID();
        UUID userB = UUID.randomUUID();

        limiter.acquireForUser("createNews", userA, 2, 60);
        limiter.acquireForUser("createNews", userA, 2, 60);

        // userB has an independent bucket.
        assertThatCode(() -> limiter.acquireForUser("createNews", userB, 2, 60))
                .doesNotThrowAnyException();
    }
}
