package com.gucardev.springreactboilerplate.infra.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verifies the {@link SecurityConfig} filter chain end-to-end: an unauthenticated request
 * to a protected endpoint returns the {@code ApiError} envelope (not the default empty 401),
 * while a path in {@code security.ignored-paths} is reachable without auth.
 *
 * <p>Uses a real server ({@code RANDOM_PORT}) on purpose — the MOCK {@code RestTestClient}
 * does not install the Spring Security filter chain, so filter-level 401/403 can only be
 * exercised over real HTTP. (Hence this does not extend the MOCK {@code BaseIntegrationTest}.)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@Import(SecurityConfigIT.ProtectedController.class)
class SecurityConfigIT {

    @Autowired
    private RestTestClient client;

    @Test
    void unauthenticatedRequest_returns401_apiErrorEnvelope() {
        client.get().uri("/__secured/ping").header("Accept-Language", "en")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.status").isEqualTo(401)
                .jsonPath("$.businessErrorCode").isEqualTo("AUTHENTICATION_REQUIRED")
                .jsonPath("$.message").isEqualTo("Authentication is required to access this resource.");
    }

    @Test
    void permittedPath_isReachableWithoutAuth() {
        // /v3/api-docs is in security.ignored-paths -> must NOT be a 401
        client.get().uri("/v3/api-docs")
                .exchange()
                .expectStatus().isOk();
    }

    @RestController
    @RequestMapping("/__secured")
    static class ProtectedController {

        @GetMapping("/ping")
        String ping() {
            return "pong";
        }
    }
}
