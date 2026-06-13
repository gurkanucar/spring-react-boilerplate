package com.gucardev.springreactboilerplate.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import com.gucardev.springreactboilerplate.domain.auth.model.request.LoginRequest;
import com.gucardev.springreactboilerplate.domain.auth.model.request.LogoutRequest;
import com.gucardev.springreactboilerplate.domain.auth.model.request.RefreshTokenRequest;
import com.gucardev.springreactboilerplate.domain.auth.model.request.RegisterRequest;
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
class AuthIT extends BaseIntegrationTest {

    // Seeded by DataSeeder under the 'test' profile.
    private static final String ADMIN_EMAIL = "admin@mail.com";
    private static final String ADMIN_PASSWORD = "pass";

    @Test
    void register_returnsTokensAndUserWithUserRole() {
        JsonNode data = postJson("/auth/register",
                new RegisterRequest("alice@mail.com", "secret123", "Alice", "Smith", "+1-555-0100"), 201)
                .path("data");

        assertThat(data.path("accessToken").asText()).isNotBlank();
        assertThat(data.path("refreshToken").asText()).isNotBlank();
        assertThat(data.path("tokenType").asText()).isEqualTo("Bearer");
        assertThat(data.path("user").path("email").asText()).isEqualTo("alice@mail.com");
        assertThat(data.path("user").path("roles").toString()).contains("USER");
        assertThat(data.path("user").has("password")).isFalse();
    }

    @Test
    void register_withDuplicateEmail_returnsConflict() {
        RegisterRequest req = new RegisterRequest("dup@mail.com", "secret123", "Dup", null, null);
        postJson("/auth/register", req, 201);

        JsonNode body = postJson("/auth/register", req, 409);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("EMAIL_ALREADY_EXISTS");
    }

    @Test
    void login_returnsTokens() {
        JsonNode data = postJson("/auth/login", new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD), 200).path("data");
        assertThat(data.path("accessToken").asText()).isNotBlank();
        assertThat(data.path("user").path("roles").toString()).contains("ADMIN");
    }

    @Test
    void login_withWrongPassword_returnsUnauthorized() {
        JsonNode body = postJson("/auth/login", new LoginRequest(ADMIN_EMAIL, "wrong-password"), 401);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("AUTHENTICATION_FAILED");
    }

    @Test
    @WithUserDetails(ADMIN_EMAIL)
    void me_returnsCurrentUser() {
        JsonNode data = getJson("/auth/me", 200).path("data");
        assertThat(data.path("email").asText()).isEqualTo(ADMIN_EMAIL);
        assertThat(data.path("roles").toString()).contains("ADMIN");
    }

    @Test
    void me_withoutAuthentication_returnsUnauthorized() {
        getJson("/auth/me", 401);
    }

    @Test
    void refresh_rotatesToken_andOldTokenIsRejected() {
        String oldRefresh = postJson("/auth/register",
                new RegisterRequest("bob@mail.com", "secret123", "Bob", null, null), 201)
                .path("data").path("refreshToken").asText();

        JsonNode refreshed = postJson("/auth/refresh", new RefreshTokenRequest(oldRefresh), 200);
        String newRefresh = refreshed.path("data").path("refreshToken").asText();
        assertThat(refreshed.path("data").path("accessToken").asText()).isNotBlank();
        assertThat(newRefresh).isNotBlank().isNotEqualTo(oldRefresh);

        // The rotated-away token must no longer be usable.
        JsonNode reused = postJson("/auth/refresh", new RefreshTokenRequest(oldRefresh), 401);
        assertThat(reused.path("businessErrorCode").asText()).isEqualTo("INVALID_REFRESH_TOKEN");
    }

    @Test
    void logout_revokesRefreshToken() {
        String refresh = postJson("/auth/register",
                new RegisterRequest("carol@mail.com", "secret123", "Carol", null, null), 201)
                .path("data").path("refreshToken").asText();

        postJson("/auth/logout", new LogoutRequest(refresh), 200);

        JsonNode afterLogout = postJson("/auth/refresh", new RefreshTokenRequest(refresh), 401);
        assertThat(afterLogout.path("businessErrorCode").asText()).isEqualTo("INVALID_REFRESH_TOKEN");
    }

    @Test
    void register_withInvalidPayload_returnsBadRequest() {
        postJson("/auth/register", new RegisterRequest("not-an-email", "", "X", null, null), 400);
    }
}
