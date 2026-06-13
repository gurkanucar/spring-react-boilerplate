package com.gucardev.springreactboilerplate.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * End-to-end coverage of the admin User/Role management endpoints: CRUD, role assignment and
 * resolution, paged filtering, and security (admin-only, 401 without a token, 403 for a
 * non-admin). Authenticates as the seeded admin to obtain a bearer token.
 */
class UserRoleManagementIT extends BaseIntegrationTest {

    private static final String ADMIN_EMAIL = "admin@mail.com";
    private static final String ADMIN_PASSWORD = "pass";

    @Test
    void role_crud_roundTrip() {
        String admin = adminToken();

        JsonNode created = postJson("/api/v1/roles",
                Map.of("name", "MANAGER", "displayName", "Manager", "description", "Manages staff"),
                admin, 201);
        Long id = created.path("data").path("id").asLong();
        assertThat(created.path("data").path("name").asText()).isEqualTo("MANAGER");

        // Duplicate name -> conflict.
        postJson("/api/v1/roles", Map.of("name", "MANAGER"), admin, 409);

        // Filtered list returns the new role.
        JsonNode list = getJson("/api/v1/roles?name=man", admin, 200);
        assertThat(list.path("data").toString()).contains("MANAGER");

        // Update only descriptive fields.
        JsonNode updated = putJson("/api/v1/roles/" + id,
                Map.of("displayName", "Senior Manager"), admin, 200);
        assertThat(updated.path("data").path("displayName").asText()).isEqualTo("Senior Manager");

        deleteJson("/api/v1/roles/" + id, admin, 200);
        getJson("/api/v1/roles/" + id, admin, 404);
    }

    @Test
    void user_crud_roundTrip_withRoleAssignment() {
        String admin = adminToken();

        JsonNode created = postJson("/api/v1/users", Map.of(
                "email", "managed@mail.com",
                "password", "secret123",
                "name", "Managed",
                "surname", "User",
                "roles", List.of("USER")), admin, 201);
        String id = created.path("data").path("id").asText();
        assertThat(created.path("data").path("email").asText()).isEqualTo("managed@mail.com");
        assertThat(created.path("data").path("roles").toString()).contains("USER");
        assertThat(created.path("data").has("password")).isFalse();

        // Read back.
        JsonNode fetched = getJson("/api/v1/users/" + id, admin, 200);
        assertThat(fetched.path("data").path("email").asText()).isEqualTo("managed@mail.com");

        // Update: disable + swap roles.
        JsonNode updated = putJson("/api/v1/users/" + id,
                Map.of("isActive", false, "roles", List.of("ADMIN", "USER")), admin, 200);
        assertThat(updated.path("data").path("isActive").asBoolean()).isFalse();
        assertThat(updated.path("data").path("roles").toString()).contains("ADMIN");

        // Filtered list.
        JsonNode list = getJson("/api/v1/users?email=managed", admin, 200);
        assertThat(list.path("data").toString()).contains("managed@mail.com");

        // Delete -> gone.
        deleteJson("/api/v1/users/" + id, admin, 200);
        getJson("/api/v1/users/" + id, admin, 404);
    }

    @Test
    void createUser_withUnknownRole_returnsBadRequest() {
        JsonNode body = postJson("/api/v1/users", Map.of(
                "email", "badrole@mail.com",
                "password", "secret123",
                "name", "Bad",
                "roles", List.of("DOES_NOT_EXIST")), adminToken(), 400);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("ROLE_NOT_FOUND");
    }

    @Test
    void createUser_withDuplicateEmail_returnsConflict() {
        JsonNode body = postJson("/api/v1/users", Map.of(
                "email", ADMIN_EMAIL,
                "password", "secret123",
                "name", "Clash"), adminToken(), 409);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("EMAIL_ALREADY_EXISTS");
    }

    @Test
    void managementEndpoints_requireAuthentication() {
        getJson("/api/v1/users", 401);
        getJson("/api/v1/roles", 401);
    }

    @Test
    void managementEndpoints_forbiddenForNonAdmin() {
        // Register a plain USER and use its token.
        String userToken = postJson("/auth/register", Map.of(
                "email", "plain@mail.com",
                "password", "secret123",
                "name", "Plain"), 201).path("data").path("accessToken").asText();

        getJson("/api/v1/users", userToken, 403);
        postJson("/api/v1/roles", Map.of("name", "X"), userToken, 403);
    }

    private String adminToken() {
        return postJson("/auth/login", Map.of("email", ADMIN_EMAIL, "password", ADMIN_PASSWORD), 200)
                .path("data").path("accessToken").asText();
    }
}
