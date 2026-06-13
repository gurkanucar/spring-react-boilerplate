package com.gucardev.springreactboilerplate.domain.workspace.service.usecase;

import com.gucardev.springreactboilerplate.domain.workspace.entity.Workspace;
import com.gucardev.springreactboilerplate.domain.workspace.exception.WorkspaceExceptionType;
import com.gucardev.springreactboilerplate.domain.workspace.mapper.WorkspaceMapper;
import com.gucardev.springreactboilerplate.domain.workspace.model.dto.WorkspaceResponseDto;
import com.gucardev.springreactboilerplate.domain.workspace.model.request.CreateWorkspaceRequest;
import com.gucardev.springreactboilerplate.domain.workspace.repository.WorkspaceRepository;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantExceptionType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public WorkspaceResponseDto execute(CreateWorkspaceRequest request) {
        if (repository.existsBySlug(request.slug())) {
            throw WorkspaceExceptionType.SLUG_ALREADY_EXISTS.toException(request.slug());
        }
        Workspace workspace = mapper.toEntity(request);
        workspace.setOrganizationId(resolveOrganizationId(request.organizationId()));
        workspace.setIsActive(request.isActive() == null || request.isActive());
        return mapper.toDto(repository.save(workspace));
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
