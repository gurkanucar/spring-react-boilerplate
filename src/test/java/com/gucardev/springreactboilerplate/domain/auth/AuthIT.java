package com.gucardev.springreactboilerplate.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseMockMvcTest;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Coverage of the auth flow at the controller level: registration auto-login, credential login,
 * refresh-token rotation, logout revocation, the authenticated {@code /me} endpoint, and the main
 * failure modes. Authentication for {@code /me} uses {@code @WithUserDetails} (the real
 * {@code CustomUserDetailsService} loads the seeded admin) rather than a real bearer token — the
 * real-token path is left for browser/e2e (Playwright) tests. Tokens are still issued and consumed
 * via the request/response bodies, since that is the feature under test.
 */
class AuthIT extends BaseMockMvcTest {

    // Seeded by DataSeeder under the 'test' profile.
    private static final String ADMIN_EMAIL = "admin@mail.com";
    private static final String ADMIN_PASSWORD = "pass";

    @Test
    void register_returnsTokensAndUserWithUserRole() throws Exception {
        JsonNode data = postJson("/auth/register",
                Map.of("email", "alice@mail.com", "password", "secret123",
                        "name", "Alice", "surname", "Smith", "phoneNumber", "+1-555-0100"),
                201).path("data");

        assertThat(data.path("accessToken").asText()).isNotBlank();
        assertThat(data.path("refreshToken").asText()).isNotBlank();
        assertThat(data.path("tokenType").asText()).isEqualTo("Bearer");
        assertThat(data.path("user").path("email").asText()).isEqualTo("alice@mail.com");
        assertThat(data.path("user").path("roles").toString()).contains("USER");
        assertThat(data.path("user").has("password")).isFalse();
    }

    @Test
    void register_withDuplicateEmail_returnsConflict() throws Exception {
        Map<String, String> req = Map.of("email", "dup@mail.com", "password", "secret123", "name", "Dup");
        postJson("/auth/register", req, 201);

        JsonNode body = postJson("/auth/register", req, 409);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("EMAIL_ALREADY_EXISTS");
    }

    @Test
    void login_returnsTokens() throws Exception {
        JsonNode data = postJson("/auth/login",
                Map.of("email", ADMIN_EMAIL, "password", ADMIN_PASSWORD), 200).path("data");
        assertThat(data.path("accessToken").asText()).isNotBlank();
        assertThat(data.path("user").path("roles").toString()).contains("ADMIN");
    }

    @Test
    void login_withWrongPassword_returnsUnauthorized() throws Exception {
        JsonNode body = postJson("/auth/login",
                Map.of("email", ADMIN_EMAIL, "password", "wrong-password"), 401);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("AUTHENTICATION_FAILED");
    }

    @Test
    @WithUserDetails(ADMIN_EMAIL)
    void me_returnsCurrentUser() throws Exception {
        JsonNode data = getJson("/auth/me", 200).path("data");
        assertThat(data.path("email").asText()).isEqualTo(ADMIN_EMAIL);
        assertThat(data.path("roles").toString()).contains("ADMIN");
    }

    @Test
    void me_withoutAuthentication_returnsUnauthorized() throws Exception {
        getJson("/auth/me", 401);
    }

    @Test
    void refresh_rotatesToken_andOldTokenIsRejected() throws Exception {
        String oldRefresh = postJson("/auth/register",
                Map.of("email", "bob@mail.com", "password", "secret123", "name", "Bob"), 201)
                .path("data").path("refreshToken").asText();

        JsonNode refreshed = postJson("/auth/refresh", Map.of("refreshToken", oldRefresh), 200);
        String newRefresh = refreshed.path("data").path("refreshToken").asText();
        assertThat(refreshed.path("data").path("accessToken").asText()).isNotBlank();
        assertThat(newRefresh).isNotBlank().isNotEqualTo(oldRefresh);

        // The rotated-away token must no longer be usable.
        JsonNode reused = postJson("/auth/refresh", Map.of("refreshToken", oldRefresh), 401);
        assertThat(reused.path("businessErrorCode").asText()).isEqualTo("INVALID_REFRESH_TOKEN");
    }

    @Test
    void logout_revokesRefreshToken() throws Exception {
        String refresh = postJson("/auth/register",
                Map.of("email", "carol@mail.com", "password", "secret123", "name", "Carol"), 201)
                .path("data").path("refreshToken").asText();

        postJson("/auth/logout", Map.of("refreshToken", refresh), 200);

        JsonNode afterLogout = postJson("/auth/refresh", Map.of("refreshToken", refresh), 401);
        assertThat(afterLogout.path("businessErrorCode").asText()).isEqualTo("INVALID_REFRESH_TOKEN");
    }

    @Test
    void register_withInvalidPayload_returnsBadRequest() throws Exception {
        postJson("/auth/register", Map.of("email", "not-an-email", "password", "", "name", "X"), 400);
    }
}
