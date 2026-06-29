package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import java.util.UUID;

/**
 * Input port: update an existing workspace. Null command fields are left unchanged.
 */
public interface UpdateWorkspaceUseCase {

    Workspace update(UUID id, UpdateWorkspaceCommand command);
}
