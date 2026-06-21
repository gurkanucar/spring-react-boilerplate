package com.gucardev.springreactboilerplate.features.tenancy.workspace.service.usecase;

import com.gucardev.springreactboilerplate.features.shared.event.WorkspaceCreatedEvent;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.entity.Workspace;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.exception.WorkspaceExceptionType;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.mapper.WorkspaceMapper;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.dto.WorkspaceResponseDto;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.request.CreateWorkspaceRequest;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.repository.WorkspaceRepository;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantExceptionType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates a workspace in the caller's organization. Org users always create in their own org; a
 * super-admin must name the target organization in the request.
 */
@Service
@RequiredArgsConstructor
public class CreateWorkspaceUseCase {

    private final WorkspaceRepository repository;
    private final WorkspaceMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public WorkspaceResponseDto execute(CreateWorkspaceRequest request) {
        if (repository.existsBySlug(request.slug())) {
            throw WorkspaceExceptionType.SLUG_ALREADY_EXISTS.toException(request.slug());
        }
        Workspace workspace = mapper.toEntity(request);
        workspace.setOrganizationId(resolveOrganizationId(request.organizationId()));
        workspace.setIsActive(request.isActive() == null || request.isActive());
        Workspace saved = repository.save(workspace);
        // Notify interested features (e.g. feature flags seed their catalog defaults) within this tx.
        eventPublisher.publishEvent(new WorkspaceCreatedEvent(saved.getId()));
        return mapper.toDto(saved);
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
