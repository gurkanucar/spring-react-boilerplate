package com.gucardev.springreactboilerplate.domain.organization;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import com.gucardev.springreactboilerplate.domain.organization.model.request.CreateOrganizationRequest;
import com.gucardev.springreactboilerplate.domain.organization.model.request.UpdateOrganizationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Admin CRUD over organizations, plus slug-uniqueness/validation and the admin-only authorization.
 */
@WithMockUser(roles = "ADMIN")
class OrganizationIT extends BaseIntegrationTest {

    @Test
    void crud_roundTrip() {
        JsonNode created = postJson("/api/v1/organizations",
                new CreateOrganizationRequest("Acme Inc.", "acme", "desc", "+1-555-0100", "1 Main St", null, null), 201);
        String id = created.path("data").path("id").asText();
        assertThat(created.path("data").path("slug").asText()).isEqualTo("acme");
        assertThat(created.path("data").path("isActive").asBoolean()).isTrue();

        // Duplicate slug -> conflict.
        JsonNode dup = postJson("/api/v1/organizations",
                new CreateOrganizationRequest("Other", "acme", null, null, null, null, null), 409);
        assertThat(dup.path("businessErrorCode").asText()).isEqualTo("ORGANIZATION_SLUG_EXISTS");

        getJson("/api/v1/organizations/" + id, 200);
        assertThat(getJson("/api/v1/organizations?name=acme", 200).path("data").toString()).contains("acme");

        JsonNode updated = putJson("/api/v1/organizations/" + id,
                new UpdateOrganizationRequest("Acme Renamed", null, null, null, null, null, null), 200);
        assertThat(updated.path("data").path("name").asText()).isEqualTo("Acme Renamed");

        deleteJson("/api/v1/organizations/" + id, 200);
        getJson("/api/v1/organizations/" + id, 404);
    }

    @Test
    void create_withInvalidSlug_returnsBadRequest() {
        postJson("/api/v1/organizations",
                new CreateOrganizationRequest("X", "Bad Slug!", null, null, null, null, null), 400);
    }

    @Test
    @WithMockUser(roles = "USER")
    void nonAdmin_isForbidden() {
        getJson("/api/v1/organizations", 403);
    }

    @Test
    @WithAnonymousUser
    void anonymous_isUnauthorized() {
        client.get().uri("/api/v1/organizations").exchange().expectStatus().isUnauthorized();
    }
}
