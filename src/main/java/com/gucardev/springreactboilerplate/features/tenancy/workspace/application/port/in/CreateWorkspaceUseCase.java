package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;

/**
 * Input port: create a workspace in the caller's organization. Driving adapters depend on this
 * interface, not on the implementing service.
 */
public interface CreateWorkspaceUseCase {

    Workspace create(CreateWorkspaceCommand command);
}
