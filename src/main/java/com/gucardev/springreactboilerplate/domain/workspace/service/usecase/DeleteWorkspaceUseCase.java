package com.gucardev.springreactboilerplate.domain.workspace.service.usecase;

import com.gucardev.springreactboilerplate.domain.workspace.repository.WorkspaceRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteWorkspaceUseCase {

    private final WorkspaceFinder finder;
    private final WorkspaceRepository repository;

    @Transactional
    public void execute(UUID id) {
        repository.delete(finder.findById(id));
    }
}
