package com.gucardev.springreactboilerplate.infra.config.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gucardev.springreactboilerplate.infra.config.security.jwt.UserPrincipal;
import com.gucardev.springreactboilerplate.infra.exception.model.BusinessException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

/**
 * Verifies the {@link RateLimited} aspect: limits are per-(method, principal). Uses a throwaway bean
 * with a tiny capacity and drives the authenticated principal directly via the SecurityContext.
 */
@SpringBootTest
@ActiveProfiles("test")
class MethodRateLimitTest {

    @TestConfiguration
    static class Config {
        @Bean
        RateLimitedDemo rateLimitedDemo() {
            return new RateLimitedDemo();
        }
    }

    static class RateLimitedDemo {
        @RateLimited(key = RateLimited.Key.USER, capacity = 2, refillSeconds = 60)
        public String run() {
            return "ok";
        }
    }

    @Autowired
    private RateLimitedDemo demo;

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void thirdCallByTheSameUser_isRateLimited() {
        authAs(UUID.randomUUID());

        assertThat(demo.run()).isEqualTo("ok");
        assertThat(demo.run()).isEqualTo("ok");
        assertThatThrownBy(demo::run)
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getCode())
                .isEqualTo("RATE_LIMIT_EXCEEDED");
    }

    @Test
    void limitIsPerUser_notShared() {
        authAs(UUID.randomUUID());
        demo.run();
        demo.run();

        // A different user has their own bucket.
        authAs(UUID.randomUUID());
        assertThat(demo.run()).isEqualTo("ok");
    }

    private void authAs(UUID userId) {
        UserPrincipal principal = new UserPrincipal(userId, null, null, "u@mail.com", "x", true, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of()));
    }
}
