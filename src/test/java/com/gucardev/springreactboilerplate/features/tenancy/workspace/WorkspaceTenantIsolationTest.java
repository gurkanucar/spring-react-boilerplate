package com.gucardev.springreactboilerplate.features.tenancy.workspace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.dto.WorkspaceResponseDto;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.request.CreateWorkspaceRequest;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.request.WorkspaceFilterRequest;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.service.usecase.CreateWorkspaceUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.service.usecase.GetAllWorkspacesUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.service.usecase.WorkspaceFinder;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContext;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import com.gucardev.springreactboilerplate.infra.exception.model.BusinessException;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verifies org-level tenant isolation at the component level by driving the {@link TenantContextHolder}
 * directly (an org user's principal, not a super-admin): a workspace is created in the caller's org,
 * the list is scoped to that org, and a cross-org fetch is reported as {@code NOT_FOUND}.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WorkspaceTenantIsolationTest {

    @Autowired
    private CreateWorkspaceUseCase createWorkspace;
    @Autowired
    private WorkspaceFinder workspaceFinder;
    @Autowired
    private GetAllWorkspacesUseCase getAllWorkspaces;

    @AfterEach
    void clearTenant() {
        TenantContextHolder.clear();
    }

    @Test
    void create_scopesToOrg_andCrossOrgFetchIsNotFound() {
        UUID orgA = UUID.randomUUID();
        UUID orgB = UUID.randomUUID();

        asOrgUser(orgA);
        WorkspaceResponseDto created = createWorkspace.execute(workspaceRequest("WS A", "ws-a"));
        assertThat(created.getOrganizationId()).isEqualTo(orgA);

        // Same org: visible.
        assertThat(workspaceFinder.findById(created.getId()).getId()).isEqualTo(created.getId());

        // Other org: looks like it doesn't exist.
        asOrgUser(orgB);
        assertThatThrownBy(() -> workspaceFinder.findById(created.getId()))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getCode())
                .isEqualTo("NOT_FOUND");
    }

    @Test
    void list_isScopedToCallersOrg() {
        UUID orgA = UUID.randomUUID();
        UUID orgB = UUID.randomUUID();

        asOrgUser(orgA);
        createWorkspace.execute(workspaceRequest("A One", "a-one"));
        asOrgUser(orgB);
        createWorkspace.execute(workspaceRequest("B One", "b-one"));

        asOrgUser(orgA);
        var page = getAllWorkspaces.execute(new WorkspaceFilterRequest());
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent()).allMatch(w -> w.getOrganizationId().equals(orgA));
    }

    private void asOrgUser(UUID organizationId) {
        TenantContextHolder.set(new TenantContext(organizationId, null, false));
    }

    private CreateWorkspaceRequest workspaceRequest(String name, String slug) {
        return new CreateWorkspaceRequest(name, slug, null, null, null, null, null, null, null);
    }
}
