package com.gucardev.springreactboilerplate.infra.config.tenant;

import java.util.UUID;

/**
 * The tenant scope of the current request: the caller's organization, the active workspace (from
 * the {@code X-Workspace-Id} header, not yet validated against the org), and whether the caller is a
 * global super-admin (an {@code ADMIN} who bypasses tenant isolation).
 */
public record TenantContext(UUID organizationId, UUID workspaceId, boolean superAdmin) {

    public boolean hasOrganization() {
        return organizationId != null;
    }

    public boolean hasWorkspace() {
        return workspaceId != null;
    }
}
