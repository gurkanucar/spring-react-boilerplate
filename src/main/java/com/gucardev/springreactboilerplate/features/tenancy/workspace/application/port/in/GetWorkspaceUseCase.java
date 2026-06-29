package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import java.util.UUID;

/**
 * Input port: read a single workspace, tenant-scoped to the caller's organization (a super-admin
 * sees all). A missing or cross-tenant id is reported as not found.
 */
public interface GetWorkspaceUseCase {

    Workspace getById(UUID id);
}
