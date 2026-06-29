package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in;

import java.util.UUID;

/**
 * Input port: delete a workspace (tenant-scoped). Driving adapters depend on this interface, not on
 * the implementing service.
 */
public interface DeleteWorkspaceUseCase {

    void delete(UUID id);
}
