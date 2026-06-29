package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.service;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.exception.WorkspaceExceptionType;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.LoadWorkspacePort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantExceptionType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Tenant-aware "fetch or 404" lookup: returns the workspace only if it belongs to the caller's
 * organization (a super-admin sees all). A cross-tenant id is reported as {@code NOT_FOUND} so it
 * never reveals that the workspace exists in another organization.
 */
@Service
@RequiredArgsConstructor
public class WorkspaceFinder {

    private final LoadWorkspacePort loadWorkspacePort;

    public Workspace findById(UUID id) {
        Workspace workspace = loadWorkspacePort.findById(id)
                .orElseThrow(() -> WorkspaceExceptionType.NOT_FOUND.toException(id));
        if (!TenantContextHolder.isSuperAdmin()) {
            UUID organizationId = TenantContextHolder.requireOrganizationId();
            if (!workspace.getOrganizationId().equals(organizationId)) {
                throw TenantExceptionType.CROSS_ORGANIZATION.toException();
            }
        }
        return workspace;
    }
}
