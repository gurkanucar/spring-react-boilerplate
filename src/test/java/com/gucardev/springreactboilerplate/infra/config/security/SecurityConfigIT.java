package com.gucardev.springreactboilerplate.infra.config.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gucardev.springreactboilerplate.BaseMockMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verifies the {@link SecurityConfig} filter chain: an unauthenticated request to a protected
 * endpoint returns the {@code ApiError} envelope (not the default empty 401), while a path in
 * {@code security.ignored-paths} is reachable without auth. The full security filter chain runs
 * under MockMvc (installed by {@code @AutoConfigureMockMvc}).
 */
@Import(SecurityConfigIT.ProtectedController.class)
class SecurityConfigIT extends BaseMockMvcTest {

    @Test
    void unauthenticatedRequest_returns401_apiErrorEnvelope() throws Exception {
        mockMvc.perform(get("/__secured/ping").header("Accept-Language", "en"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.businessErrorCode").value("AUTHENTICATION_REQUIRED"))
                .andExpect(jsonPath("$.message").value("Authentication is required to access this resource."));
    }

    @Test
    void permittedPath_isReachableWithoutAuth() throws Exception {
        // /v3/api-docs is in security.ignored-paths -> must NOT be a 401
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
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
