package com.gucardev.springreactboilerplate.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import com.gucardev.springreactboilerplate.domain.auth.model.request.LoginRequest;
import com.gucardev.springreactboilerplate.domain.auth.model.request.LogoutRequest;
import com.gucardev.springreactboilerplate.domain.auth.model.request.RefreshTokenRequest;
import com.gucardev.springreactboilerplate.domain.auth.model.request.RegisterRequest;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * End-to-end coverage of the JWT auth flow: registration auto-login, credential login, the
 * authenticated {@code /me} endpoint, refresh-token rotation, logout revocation and the main
 * failure modes (duplicate email, bad credentials, missing/invalid tokens).
 */
class AuthIT extends BaseIntegrationTest {

    // Seeded by DataSeeder under the 'test' profile.
    private static final String ADMIN_EMAIL = "admin@mail.com";
    private static final String ADMIN_PASSWORD = "pass";

    @Test
    void register_returnsTokensAndUserWithUserRole() {
        JsonNode body = postJson("/auth/register",
                new RegisterRequest("alice@mail.com", "secret123", "Alice", "Smith", "+1-555-0100"),
                201);

        assertThat(body.path("success").asBoolean()).isTrue();
        JsonNode data = body.path("data");
        assertThat(data.path("accessToken").asText()).isNotBlank();
        assertThat(data.path("refreshToken").asText()).isNotBlank();
        assertThat(data.path("tokenType").asText()).isEqualTo("Bearer");
        assertThat(data.path("user").path("email").asText()).isEqualTo("alice@mail.com");
        assertThat(data.path("user").path("roles").toString()).contains("USER");
        // Password must never be exposed.
        assertThat(data.path("user").has("password")).isFalse();
    }

    @Test
    void register_withDuplicateEmail_returnsConflict() {
        RegisterRequest req = new RegisterRequest("dup@mail.com", "secret123", "Dup", "User", null);
        postJson("/auth/register", req, 201);

        JsonNode body = postJson("/auth/register", req, 409);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("EMAIL_ALREADY_EXISTS");
    }

    @Test
    void login_thenAccessMe_returnsCurrentUser() {
        String accessToken = login(ADMIN_EMAIL, ADMIN_PASSWORD).path("data").path("accessToken").asText();

        JsonNode me = getJson("/auth/me", accessToken, 200);

        assertThat(me.path("data").path("email").asText()).isEqualTo(ADMIN_EMAIL);
        assertThat(me.path("data").path("roles").toString()).contains("ADMIN");
    }

    @Test
    void me_withoutToken_returnsUnauthorized() {
        getJson("/auth/me", 401);
    }

    @Test
    void me_withGarbageToken_returnsUnauthorized() {
        getJson("/auth/me", "not-a-real-token", 401);
    }

    @Test
    void login_withWrongPassword_returnsUnauthorized() {
        JsonNode body = postJson("/auth/login", new LoginRequest(ADMIN_EMAIL, "wrong-password"), 401);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("AUTHENTICATION_FAILED");
    }

    @Test
    void refresh_rotatesToken_andOldTokenIsRejected() {
        postJson("/auth/register",
                new RegisterRequest("bob@mail.com", "secret123", "Bob", "Jones", null), 201);
        String oldRefresh = login("bob@mail.com", "secret123").path("data").path("refreshToken").asText();

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
        postJson("/auth/register",
                new RegisterRequest("carol@mail.com", "secret123", "Carol", "White", null), 201);
        String refresh = login("carol@mail.com", "secret123").path("data").path("refreshToken").asText();

        postJson("/auth/logout", new LogoutRequest(refresh), 200);

        JsonNode afterLogout = postJson("/auth/refresh", new RefreshTokenRequest(refresh), 401);
        assertThat(afterLogout.path("businessErrorCode").asText()).isEqualTo("INVALID_REFRESH_TOKEN");
    }

    @Test
    void register_withInvalidPayload_returnsBadRequest() {
        // blank password + invalid email -> bean validation 400
        postJson("/auth/register", Map.of("email", "not-an-email", "password", "", "name", "X"), 400);
    }

    private JsonNode login(String email, String password) {
        return postJson("/auth/login", new LoginRequest(email, password), 200);
    }
}
