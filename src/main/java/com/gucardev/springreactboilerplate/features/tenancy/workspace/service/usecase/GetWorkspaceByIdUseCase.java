package com.gucardev.springreactboilerplate.features.tenancy.workspace.service.usecase;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.mapper.WorkspaceMapper;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.dto.WorkspaceResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetWorkspaceByIdUseCase {

    private final WorkspaceFinder finder;
    private final WorkspaceMapper mapper;

    @Transactional(readOnly = true)
    public WorkspaceResponseDto execute(UUID id) {
        return mapper.toDto(finder.findById(id));
    }
}
