package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;

/**
 * Output port: delete a workspace from the store. Implemented by a driven persistence adapter.
 */
public interface DeleteWorkspacePort {

    void delete(Workspace workspace);
}
