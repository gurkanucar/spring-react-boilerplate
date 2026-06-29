package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import org.springframework.data.domain.Page;

/**
 * Input port: list workspaces in the caller's organization (paged, sorted and filtered). Org users
 * are constrained to their own org; a super-admin may filter by any org (or all).
 */
public interface ListWorkspacesUseCase {

    Page<Workspace> list(ListWorkspacesQuery query);
}
