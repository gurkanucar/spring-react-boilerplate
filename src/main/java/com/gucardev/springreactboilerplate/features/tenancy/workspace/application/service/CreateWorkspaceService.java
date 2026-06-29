package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.service;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.exception.WorkspaceExceptionType;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.CreateWorkspaceCommand;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.CreateWorkspaceUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.LoadWorkspacePort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.SaveWorkspacePort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.WorkspaceEventPublisherPort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantExceptionType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates a workspace in the caller's organization. Org users always create in their own org; a
 * super-admin must name the target organization in the command.
 *
 * <p>Saving the workspace AND publishing the {@code WorkspaceCreated} event happen in one
 * transaction, so interested features (e.g. feature flags seeding their catalog defaults) react
 * atomically with the create.
 */
@Service
@RequiredArgsConstructor
public class CreateWorkspaceService implements CreateWorkspaceUseCase {

    private final LoadWorkspacePort loadWorkspacePort;
    private final SaveWorkspacePort saveWorkspacePort;
    private final WorkspaceEventPublisherPort eventPublisherPort;

    @Override
    @Transactional
    public Workspace create(CreateWorkspaceCommand command) {
        if (loadWorkspacePort.existsBySlug(command.slug())) {
            throw WorkspaceExceptionType.SLUG_ALREADY_EXISTS.toException(command.slug());
        }
        Workspace workspace = Workspace.builder()
                .name(command.name())
                .slug(command.slug())
                .description(command.description())
                .phoneNumber(command.phoneNumber())
                .address(command.address())
                .brandColor(command.brandColor())
                .logoId(command.logoId())
                .organizationId(resolveOrganizationId(command.organizationId()))
                .isActive(command.isActive() == null || command.isActive())
                .build();
        Workspace saved = saveWorkspacePort.save(workspace);
        // Notify interested features within this tx.
        eventPublisherPort.publishCreated(saved.getId());
        return saved;
    }

    private UUID resolveOrganizationId(UUID requested) {
        if (TenantContextHolder.isSuperAdmin()) {
            if (requested == null) {
                throw TenantExceptionType.NO_ORGANIZATION_CONTEXT.toException();
            }
            return requested;
        }
        return TenantContextHolder.requireOrganizationId();
    }
}
