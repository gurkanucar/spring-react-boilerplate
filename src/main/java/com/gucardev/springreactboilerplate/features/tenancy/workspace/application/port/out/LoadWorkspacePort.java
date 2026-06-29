package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port: load a workspace from the store. Implemented by a driven persistence adapter.
 */
public interface LoadWorkspacePort {

    Optional<Workspace> findById(UUID id);

    boolean existsBySlug(String slug);
}
