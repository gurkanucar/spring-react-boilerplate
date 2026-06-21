package com.gucardev.springreactboilerplate.infra.config.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Verifies resilience4j's built-in {@code @RateLimiter} aspect is wired: an annotated bean method is
 * throttled (name-based, global) and over-limit calls throw {@link RequestNotPermitted} (mapped to
 * 429 by the global exception handler).
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "resilience4j.ratelimiter.instances.rlDemo.limit-for-period=2",
        "resilience4j.ratelimiter.instances.rlDemo.limit-refresh-period=60s",
        "resilience4j.ratelimiter.instances.rlDemo.timeout-duration=0s"
})
class ResilienceRateLimiterTest {

    @TestConfiguration
    static class Config {
        @Bean
        RateLimitedDemo rateLimitedDemo() {
            return new RateLimitedDemo();
        }
    }

    static class RateLimitedDemo {
        @RateLimiter(name = "rlDemo")
        public String run() {
            return "ok";
        }
    }

    @Autowired
    private RateLimitedDemo demo;

    @Test
    void thirdCall_isRejected() {
        assertThat(demo.run()).isEqualTo("ok");
        assertThat(demo.run()).isEqualTo("ok");
        assertThatThrownBy(demo::run).isInstanceOf(RequestNotPermitted.class);
    }
}
