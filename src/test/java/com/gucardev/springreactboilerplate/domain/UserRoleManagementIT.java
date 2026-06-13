package com.gucardev.springreactboilerplate.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import com.gucardev.springreactboilerplate.domain.role.model.request.CreateRoleRequest;
import com.gucardev.springreactboilerplate.domain.role.model.request.UpdateRoleRequest;
import com.gucardev.springreactboilerplate.domain.user.model.request.CreateUserRequest;
import com.gucardev.springreactboilerplate.domain.user.model.request.UpdateUserRequest;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * CRUD and business-rule coverage of the admin User/Role management endpoints, driven as an ADMIN
 * (via {@code @WithMockUser}). The authorization rules (admin-only, 401/403) live in
 * {@link AdminAuthorizationTest}.
 */
@WithMockUser(roles = "ADMIN")
class UserRoleManagementIT extends BaseIntegrationTest {

    @Test
    void role_crud_roundTrip() {
        JsonNode created = postJson("/api/v1/roles",
                new CreateRoleRequest("MANAGER", "Manager", "Manages staff"), 201);
        long id = created.path("data").path("id").asLong();
        assertThat(created.path("data").path("name").asText()).isEqualTo("MANAGER");

        // Duplicate name -> conflict.
        postJson("/api/v1/roles", new CreateRoleRequest("MANAGER", null, null), 409);

        // Filtered list returns the new role.
        assertThat(getJson("/api/v1/roles?name=man", 200).path("data").toString()).contains("MANAGER");

        // Update only descriptive fields.
        JsonNode updated = putJson("/api/v1/roles/" + id, new UpdateRoleRequest("Senior Manager", null), 200);
        assertThat(updated.path("data").path("displayName").asText()).isEqualTo("Senior Manager");

        deleteJson("/api/v1/roles/" + id, 200);
        getJson("/api/v1/roles/" + id, 404);
    }

    @Test
    void user_crud_roundTrip_withRoleAssignment() {
        JsonNode created = postJson("/api/v1/users", new CreateUserRequest(
                "managed@mail.com", "secret123", "Managed", "User", null, null, null, Set.of("USER"), null, null), 201);
        String id = created.path("data").path("id").asText();
        assertThat(created.path("data").path("email").asText()).isEqualTo("managed@mail.com");
        assertThat(created.path("data").path("roles").toString()).contains("USER");
        assertThat(created.path("data").has("password")).isFalse();

        assertThat(getJson("/api/v1/users/" + id, 200).path("data").path("email").asText())
                .isEqualTo("managed@mail.com");

        // Disable + swap roles.
        JsonNode updated = putJson("/api/v1/users/" + id,
                new UpdateUserRequest(null, null, null, null, false, Set.of("ADMIN", "USER"), null, null), 200);
        assertThat(updated.path("data").path("isActive").asBoolean()).isFalse();
        assertThat(updated.path("data").path("roles").toString()).contains("ADMIN");

        assertThat(getJson("/api/v1/users?email=managed", 200).path("data").toString())
                .contains("managed@mail.com");

        deleteJson("/api/v1/users/" + id, 200);
        getJson("/api/v1/users/" + id, 404);
    }

    @Test
    void createUser_withUnknownRole_returnsBadRequest() {
        JsonNode body = postJson("/api/v1/users", new CreateUserRequest(
                "badrole@mail.com", "secret123", "Bad", null, null, null, null, Set.of("DOES_NOT_EXIST"), null, null), 400);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("ROLE_NOT_FOUND");
    }

    @Test
    void createUser_withDuplicateEmail_returnsConflict() {
        JsonNode body = postJson("/api/v1/users", new CreateUserRequest(
                "admin@mail.com", "secret123", "Clash", null, null, null, null, null, null, null), 409);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("EMAIL_ALREADY_EXISTS");
    }
}
