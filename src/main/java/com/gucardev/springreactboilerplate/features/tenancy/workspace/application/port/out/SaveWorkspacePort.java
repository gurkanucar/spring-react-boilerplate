package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;

/**
 * Output port: persist a workspace (insert or update) and return the stored state, including any
 * generated id and audit metadata.
 */
public interface SaveWorkspacePort {

    Workspace save(Workspace workspace);
}
