package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.service;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.DeleteWorkspaceUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.DeleteWorkspacePort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.WorkspaceEventPublisherPort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Deletes a workspace (tenant-scoped) and publishes a {@code WorkspaceDeleted} event so interested
 * features can clean up their workspace-scoped data within the same transaction.
 */
@Service
@RequiredArgsConstructor
public class DeleteWorkspaceService implements DeleteWorkspaceUseCase {

    private final WorkspaceFinder finder;
    private final DeleteWorkspacePort deleteWorkspacePort;
    private final WorkspaceEventPublisherPort eventPublisherPort;

    @Override
    @Transactional
    public void delete(UUID id) {
        Workspace workspace = finder.findById(id);
        deleteWorkspacePort.delete(workspace);
        // Notify interested features to clean up their workspace-scoped data within this tx.
        eventPublisherPort.publishDeleted(id);
    }
}
