package com.gucardev.springreactboilerplate.features.tenancy.workspace.service.usecase;

import com.gucardev.springreactboilerplate.features.shared.event.WorkspaceDeletedEvent;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.repository.WorkspaceRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteWorkspaceUseCase {

    private final WorkspaceFinder finder;
    private final WorkspaceRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void execute(UUID id) {
        repository.delete(finder.findById(id));
        // Notify interested features to clean up their workspace-scoped data within this tx.
        eventPublisher.publishEvent(new WorkspaceDeletedEvent(id));
    }
}
