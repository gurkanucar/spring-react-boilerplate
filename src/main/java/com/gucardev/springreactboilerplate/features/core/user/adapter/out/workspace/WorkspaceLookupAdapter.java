package com.gucardev.springreactboilerplate.features.core.user.adapter.out.workspace;

import com.gucardev.springreactboilerplate.features.core.user.application.port.out.WorkspaceLookupPort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.FindWorkspaceUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing {@link WorkspaceLookupPort} by delegating to the workspace feature's input
 * port. This is the ONLY place in the user module that depends on workspace types.
 */
@Component
@RequiredArgsConstructor
public class WorkspaceLookupAdapter implements WorkspaceLookupPort {

    private final FindWorkspaceUseCase findWorkspaceUseCase;

    @Override
    public Optional<UUID> findOrganizationId(UUID workspaceId) {
        return findWorkspaceUseCase.findById(workspaceId).map(Workspace::getOrganizationId);
    }
}
