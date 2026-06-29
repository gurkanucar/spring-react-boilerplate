package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.service;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.exception.WorkspaceExceptionType;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.UpdateWorkspaceCommand;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.UpdateWorkspaceUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.LoadWorkspacePort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.SaveWorkspacePort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Updates an existing workspace (tenant-scoped). Null command fields are left unchanged; the owning
 * organization is never reassigned.
 */
@Service
@RequiredArgsConstructor
public class UpdateWorkspaceService implements UpdateWorkspaceUseCase {

    private final WorkspaceFinder finder;
    private final LoadWorkspacePort loadWorkspacePort;
    private final SaveWorkspacePort saveWorkspacePort;

    @Override
    @Transactional
    public Workspace update(UUID id, UpdateWorkspaceCommand command) {
        Workspace workspace = finder.findById(id);
        if (command.slug() != null && !command.slug().equals(workspace.getSlug())
                && loadWorkspacePort.existsBySlug(command.slug())) {
            throw WorkspaceExceptionType.SLUG_ALREADY_EXISTS.toException(command.slug());
        }
        applyChanges(workspace, command);
        return saveWorkspacePort.save(workspace);
    }

    /** Mirrors MapStruct's IGNORE-null update: only non-null command fields overwrite the target. */
    private void applyChanges(Workspace workspace, UpdateWorkspaceCommand command) {
        if (command.name() != null) {
            workspace.setName(command.name());
        }
        if (command.slug() != null) {
            workspace.setSlug(command.slug());
        }
        if (command.description() != null) {
            workspace.setDescription(command.description());
        }
        if (command.phoneNumber() != null) {
            workspace.setPhoneNumber(command.phoneNumber());
        }
        if (command.address() != null) {
            workspace.setAddress(command.address());
        }
        if (command.brandColor() != null) {
            workspace.setBrandColor(command.brandColor());
        }
        if (command.isActive() != null) {
            workspace.setIsActive(command.isActive());
        }
        if (command.logoId() != null) {
            workspace.setLogoId(command.logoId());
        }
    }
}
