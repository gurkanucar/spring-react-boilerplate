package com.gucardev.springreactboilerplate.infra.config.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

/**
 * Verifies the per-IP token bucket: with capacity 2, the third hit to a limited endpoint is
 * rejected with 429 and the standard error envelope. Rate limiting is off for the rest of the
 * suite (see test application.properties); this class re-enables it with a tiny capacity.
 */
@TestPropertySource(properties = {
        "app.rate-limit.enabled=true",
        "app.rate-limit.capacity=2",
        "app.rate-limit.refill-seconds=60",
        "app.rate-limit.paths=/auth/login"
})
class RateLimitTest extends BaseIntegrationTest {

    @Test
    void thirdLoginAttempt_isRateLimited() {
        Map<String, String> body = Map.of("email", "a@a.com", "password", "x");

        // Two allowed (whatever the auth outcome), the bucket is now empty.
        client.post().uri("/auth/login").contentType(MediaType.APPLICATION_JSON).body(body).exchange();
        client.post().uri("/auth/login").contentType(MediaType.APPLICATION_JSON).body(body).exchange();

        JsonNode third = postJson("/auth/login", body, 429);
        assertThat(third.path("businessErrorCode").asText()).isEqualTo("RATE_LIMIT_EXCEEDED");
        assertThat(third.path("status").asInt()).isEqualTo(429);
    }
}
