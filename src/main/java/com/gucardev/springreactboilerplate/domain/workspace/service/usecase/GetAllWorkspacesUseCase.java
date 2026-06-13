package com.gucardev.springreactboilerplate.domain.workspace.service.usecase;

import com.gucardev.springreactboilerplate.domain.workspace.mapper.WorkspaceMapper;
import com.gucardev.springreactboilerplate.domain.workspace.model.dto.WorkspaceResponseDto;
import com.gucardev.springreactboilerplate.domain.workspace.model.request.WorkspaceFilterRequest;
import com.gucardev.springreactboilerplate.domain.workspace.repository.WorkspaceRepository;
import com.gucardev.springreactboilerplate.domain.workspace.repository.specification.WorkspaceSpecification;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllWorkspacesUseCase {

    private final WorkspaceRepository repository;
    private final WorkspaceMapper mapper;

    @Transactional(readOnly = true)
    public Page<WorkspaceResponseDto> execute(WorkspaceFilterRequest filter) {
        // Org users are constrained to their own org; a super-admin may filter by any (or all).
        UUID organizationId = TenantContextHolder.isSuperAdmin()
                ? filter.getOrganizationId()
                : TenantContextHolder.requireOrganizationId();
        return repository.findAll(WorkspaceSpecification.build(filter, organizationId), filter.toPageable())
                .map(mapper::toDto);
    }
}
