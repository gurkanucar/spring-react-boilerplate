package com.gucardev.springreactboilerplate.features.tenancy.workspace.service.usecase;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.entity.Workspace;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.exception.WorkspaceExceptionType;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.mapper.WorkspaceMapper;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.dto.WorkspaceResponseDto;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.request.UpdateWorkspaceRequest;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.repository.WorkspaceRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateWorkspaceUseCase {

    private final WorkspaceFinder finder;
    private final WorkspaceRepository repository;
    private final WorkspaceMapper mapper;

    @Transactional
    public WorkspaceResponseDto execute(UUID id, UpdateWorkspaceRequest request) {
        Workspace workspace = finder.findById(id);
        if (request.slug() != null && !request.slug().equals(workspace.getSlug())
                && repository.existsBySlug(request.slug())) {
            throw WorkspaceExceptionType.SLUG_ALREADY_EXISTS.toException(request.slug());
        }
        mapper.updateEntity(request, workspace);
        return mapper.toDto(repository.save(workspace));
    }
}
