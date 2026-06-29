package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import org.springframework.data.domain.Page;

/**
 * Output port: search workspaces by criteria with paging. Implemented by a driven persistence
 * adapter, which translates the criteria into a JPA Specification.
 */
public interface SearchWorkspacePort {

    Page<Workspace> search(WorkspaceSearchCriteria criteria);
}
