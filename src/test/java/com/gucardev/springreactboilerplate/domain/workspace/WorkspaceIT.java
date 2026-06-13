package com.gucardev.springreactboilerplate.domain.workspace;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import com.gucardev.springreactboilerplate.domain.organization.model.request.CreateOrganizationRequest;
import com.gucardev.springreactboilerplate.domain.workspace.model.request.CreateWorkspaceRequest;
import com.gucardev.springreactboilerplate.domain.workspace.model.request.UpdateWorkspaceRequest;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Workspace CRUD over HTTP as the seeded admin (a global super-admin, so the tenant filter sees the
 * real {@code UserPrincipal} and bypasses org isolation). Org-user isolation is covered at the
 * component level in {@link WorkspaceTenantIsolationTest}.
 */
@WithUserDetails("admin@mail.com")
class WorkspaceIT extends BaseIntegrationTest {

    @Test
    void superAdmin_crud_roundTrip() {
        String orgId = postJson("/api/v1/organizations",
                new CreateOrganizationRequest("Org WS", "org-ws", "d", null, null, null, null), 201)
                .path("data").path("id").asText();

        JsonNode created = postJson("/api/v1/workspaces", new CreateWorkspaceRequest(
                "Downtown", "downtown", "d", null, null, "#b8732b", null, null, UUID.fromString(orgId)), 201);
        String wsId = created.path("data").path("id").asText();
        assertThat(created.path("data").path("organizationId").asText()).isEqualTo(orgId);
        assertThat(created.path("data").path("brandColor").asText()).isEqualTo("#b8732b");

        getJson("/api/v1/workspaces/" + wsId, 200);
        assertThat(getJson("/api/v1/workspaces?organizationId=" + orgId, 200).path("data").toString())
                .contains("downtown");

        JsonNode updated = putJson("/api/v1/workspaces/" + wsId,
                new UpdateWorkspaceRequest("Uptown", null, null, null, null, null, null, null), 200);
        assertThat(updated.path("data").path("name").asText()).isEqualTo("Uptown");

        deleteJson("/api/v1/workspaces/" + wsId, 200);
        getJson("/api/v1/workspaces/" + wsId, 404);
    }

    @Test
    void superAdmin_createWithoutOrganization_isRejected() {
        JsonNode body = postJson("/api/v1/workspaces", new CreateWorkspaceRequest(
                "X", "x-ws", null, null, null, null, null, null, null), 403);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("NO_ORGANIZATION_CONTEXT");
    }
}
