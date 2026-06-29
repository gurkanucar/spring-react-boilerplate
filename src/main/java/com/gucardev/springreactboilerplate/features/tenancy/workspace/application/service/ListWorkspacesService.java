package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.service;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.ListWorkspacesQuery;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.ListWorkspacesUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.SearchWorkspacePort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.WorkspaceSearchCriteria;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lists workspaces. Org users are constrained to their own org; a super-admin may filter by any org
 * (or all). The resolved tenant scope is pushed into the search criteria.
 */
@Service
@RequiredArgsConstructor
public class ListWorkspacesService implements ListWorkspacesUseCase {

    private final SearchWorkspacePort searchWorkspacePort;

    @Override
    @Transactional(readOnly = true)
    public Page<Workspace> list(ListWorkspacesQuery query) {
        UUID organizationId = TenantContextHolder.isSuperAdmin()
                ? query.organizationId()
                : TenantContextHolder.requireOrganizationId();
        return searchWorkspacePort.search(new WorkspaceSearchCriteria(
                query.name(),
                query.isActive(),
                organizationId,
                query.startDate(),
                query.endDate(),
                query.pageable()));
    }
}
