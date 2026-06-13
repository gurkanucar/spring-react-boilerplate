package com.gucardev.springreactboilerplate.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseMockMvcTest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * CRUD and business-rule coverage of the admin User/Role management endpoints, driven through
 * MockMvc as an ADMIN (via {@code @WithMockUser}) — no real token needed. The authorization rules
 * themselves (admin-only, 401/403) are covered by {@link AdminAuthorizationTest}.
 */
@WithMockUser(roles = "ADMIN")
class UserRoleManagementIT extends BaseMockMvcTest {

    @Test
    void role_crud_roundTrip() throws Exception {
        JsonNode created = postJson("/api/v1/roles",
                Map.of("name", "MANAGER", "displayName", "Manager", "description", "Manages staff"),
                201);
        Long id = created.path("data").path("id").asLong();
        assertThat(created.path("data").path("name").asText()).isEqualTo("MANAGER");

        // Duplicate name -> conflict.
        postJson("/api/v1/roles", Map.of("name", "MANAGER"), 409);

        // Filtered list returns the new role.
        assertThat(getJson("/api/v1/roles?name=man", 200).path("data").toString()).contains("MANAGER");

        // Update only descriptive fields.
        JsonNode updated = putJson("/api/v1/roles/" + id, Map.of("displayName", "Senior Manager"), 200);
        assertThat(updated.path("data").path("displayName").asText()).isEqualTo("Senior Manager");

        deleteJson("/api/v1/roles/" + id, 200);
        getJson("/api/v1/roles/" + id, 404);
    }

    @Test
    void user_crud_roundTrip_withRoleAssignment() throws Exception {
        JsonNode created = postJson("/api/v1/users", Map.of(
                "email", "managed@mail.com",
                "password", "secret123",
                "name", "Managed",
                "surname", "User",
                "roles", List.of("USER")), 201);
        String id = created.path("data").path("id").asText();
        assertThat(created.path("data").path("email").asText()).isEqualTo("managed@mail.com");
        assertThat(created.path("data").path("roles").toString()).contains("USER");
        assertThat(created.path("data").has("password")).isFalse();

        // Read back.
        assertThat(getJson("/api/v1/users/" + id, 200).path("data").path("email").asText())
                .isEqualTo("managed@mail.com");

        // Update: disable + swap roles.
        JsonNode updated = putJson("/api/v1/users/" + id,
                Map.of("isActive", false, "roles", List.of("ADMIN", "USER")), 200);
        assertThat(updated.path("data").path("isActive").asBoolean()).isFalse();
        assertThat(updated.path("data").path("roles").toString()).contains("ADMIN");

        // Filtered list.
        assertThat(getJson("/api/v1/users?email=managed", 200).path("data").toString())
                .contains("managed@mail.com");

        // Delete -> gone.
        deleteJson("/api/v1/users/" + id, 200);
        getJson("/api/v1/users/" + id, 404);
    }

    @Test
    void createUser_withUnknownRole_returnsBadRequest() throws Exception {
        JsonNode body = postJson("/api/v1/users", Map.of(
                "email", "badrole@mail.com",
                "password", "secret123",
                "name", "Bad",
                "roles", List.of("DOES_NOT_EXIST")), 400);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("ROLE_NOT_FOUND");
    }

    @Test
    void createUser_withDuplicateEmail_returnsConflict() throws Exception {
        JsonNode body = postJson("/api/v1/users", Map.of(
                "email", "admin@mail.com",
                "password", "secret123",
                "name", "Clash"), 409);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("EMAIL_ALREADY_EXISTS");
    }
}
