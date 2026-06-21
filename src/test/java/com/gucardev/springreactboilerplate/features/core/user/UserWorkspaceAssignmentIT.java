package com.gucardev.springreactboilerplate.features.core.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import com.gucardev.springreactboilerplate.features.tenancy.organization.model.request.CreateOrganizationRequest;
import com.gucardev.springreactboilerplate.features.core.user.model.request.CreateUserRequest;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.request.CreateWorkspaceRequest;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Verifies that pinning a user to a workspace is consistent with their organization: the workspace
 * must belong to the assigned org, and a workspace pin requires an org. Runs as the seeded admin
 * (super-admin) to set up the org/workspace.
 */
@WithUserDetails("admin@mail.com")
class UserWorkspaceAssignmentIT extends BaseIntegrationTest {

    @Test
    void workspaceAssignment_mustMatchOrganization() {
        String orgA = createOrganization("WA Org A", "wa-org-a");
        String orgB = createOrganization("WA Org B", "wa-org-b");
        String wsId = postJson("/api/v1/workspaces", new CreateWorkspaceRequest(
                "WA WS", "wa-ws", "d", null, null, null, null, null, UUID.fromString(orgA)), 201)
                .path("data").path("id").asText();

        // Valid: employee in org A, pinned to A's workspace.
        JsonNode ok = postJson("/api/v1/users", new CreateUserRequest(
                "emp@mail.com", "secret123", "Emp", null, null, null, null,
                Set.of("WORKSPACE_USER"), UUID.fromString(orgA), UUID.fromString(wsId)), 201);
        assertThat(ok.path("data").path("workspaceId").asText()).isEqualTo(wsId);

        // Workspace belongs to org A, but the user is assigned to org B.
        JsonNode mismatch = postJson("/api/v1/users", new CreateUserRequest(
                "emp2@mail.com", "secret123", "Emp2", null, null, null, null,
                Set.of("USER"), UUID.fromString(orgB), UUID.fromString(wsId)), 400);
        assertThat(mismatch.path("businessErrorCode").asText()).isEqualTo("USER_WORKSPACE_NOT_IN_ORG");

        // Workspace without an organization.
        JsonNode noOrg = postJson("/api/v1/users", new CreateUserRequest(
                "emp3@mail.com", "secret123", "Emp3", null, null, null, null,
                Set.of("USER"), null, UUID.fromString(wsId)), 400);
        assertThat(noOrg.path("businessErrorCode").asText()).isEqualTo("USER_WORKSPACE_REQUIRES_ORG");

        // Non-existent organization (no workspace).
        JsonNode missingOrg = postJson("/api/v1/users", new CreateUserRequest(
                "emp4@mail.com", "secret123", "Emp4", null, null, null, null,
                Set.of("USER"), UUID.randomUUID(), null), 400);
        assertThat(missingOrg.path("businessErrorCode").asText()).isEqualTo("USER_ORGANIZATION_NOT_FOUND");
    }

    private String createOrganization(String name, String slug) {
        return postJson("/api/v1/organizations",
                new CreateOrganizationRequest(name, slug, "d", null, null, null, null), 201)
                .path("data").path("id").asText();
    }
}
