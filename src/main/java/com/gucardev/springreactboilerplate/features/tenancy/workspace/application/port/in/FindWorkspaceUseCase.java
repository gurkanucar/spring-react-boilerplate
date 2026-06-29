package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import java.util.Optional;
import java.util.UUID;

/**
 * Input port for cross-feature consumers (e.g. user tenant-assignment validation): a plain lookup of
 * a workspace by id with NO tenant scoping, returning empty when absent. Keeps other features off the
 * workspace persistence internals — they depend on this port and the domain model only.
 */
public interface FindWorkspaceUseCase {

    Optional<Workspace> findById(UUID id);
}
