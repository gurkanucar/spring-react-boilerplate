package com.gucardev.springreactboilerplate.domain;

import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import com.gucardev.springreactboilerplate.domain.role.model.request.CreateRoleRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Authorization-rule tests for the admin User/Role endpoints. These verify the
 * {@code @PreAuthorize("hasRole('ADMIN')")} rules and the entry-point/deny behaviour — not the
 * business logic (covered by {@link UserRoleManagementIT}).
 */
class AdminAuthorizationTest extends BaseIntegrationTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_canListUsers() {
        client.get().uri("/api/v1/users").exchange().expectStatus().isOk();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_canListRoles() {
        client.get().uri("/api/v1/roles").exchange().expectStatus().isOk();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_canCreateRole() {
        client.post().uri("/api/v1/roles").contentType(MediaType.APPLICATION_JSON)
                .body(new CreateRoleRequest("TESTER", "Tester", null))
                .exchange().expectStatus().isCreated();
    }

    @Test
    @WithMockUser(roles = "USER")
    void nonAdmin_isForbiddenFromUsers() {
        client.get().uri("/api/v1/users").exchange().expectStatus().isForbidden();
    }

    @Test
    @WithMockUser(roles = "USER")
    void nonAdmin_isForbiddenFromCreatingRoles() {
        client.post().uri("/api/v1/roles").contentType(MediaType.APPLICATION_JSON)
                .body(new CreateRoleRequest("X", null, null))
                .exchange().expectStatus().isForbidden();
    }

    @Test
    void anonymous_isUnauthorized() {
        client.get().uri("/api/v1/users").exchange().expectStatus().isUnauthorized();
        client.get().uri("/api/v1/roles").exchange().expectStatus().isUnauthorized();
    }
}
