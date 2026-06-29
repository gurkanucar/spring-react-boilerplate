package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.service;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.FindWorkspaceUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.GetWorkspaceUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.LoadWorkspacePort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Read-side workspace use cases:
 * <ul>
 *   <li>{@link GetWorkspaceUseCase} — tenant-scoped fetch-or-404 for the web read path.</li>
 *   <li>{@link FindWorkspaceUseCase} — plain, unscoped lookup for cross-feature consumers.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class GetWorkspaceService implements GetWorkspaceUseCase, FindWorkspaceUseCase {

    private final WorkspaceFinder finder;
    private final LoadWorkspacePort loadWorkspacePort;

    @Override
    @Transactional(readOnly = true)
    public Workspace getById(UUID id) {
        return finder.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Workspace> findById(UUID id) {
        return loadWorkspacePort.findById(id);
    }
}
