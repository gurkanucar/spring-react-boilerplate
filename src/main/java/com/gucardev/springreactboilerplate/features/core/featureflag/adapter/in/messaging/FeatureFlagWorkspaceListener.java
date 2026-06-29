package com.gucardev.springreactboilerplate.features.core.featureflag.adapter.in.messaging;

import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in.DeleteWorkspaceFeatureFlagsUseCase;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in.SeedWorkspaceFeatureFlagsUseCase;
import com.gucardev.springreactboilerplate.features.shared.event.WorkspaceCreatedEvent;
import com.gucardev.springreactboilerplate.features.shared.event.WorkspaceDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Driving (event-in) adapter keeping per-workspace feature flags in sync with the workspace lifecycle:
 * seeds the catalog defaults when a workspace is created and removes the overrides when it is deleted.
 * Listeners are synchronous, so they run inside the workspace transaction (seeding rolls back with the
 * workspace if it fails). It stays thin: it only delegates to the input ports.
 */
@Component
@RequiredArgsConstructor
public class FeatureFlagWorkspaceListener {

    private final SeedWorkspaceFeatureFlagsUseCase seedWorkspaceFeatureFlagsUseCase;
    private final DeleteWorkspaceFeatureFlagsUseCase deleteWorkspaceFeatureFlagsUseCase;

    @EventListener
    public void onWorkspaceCreated(WorkspaceCreatedEvent event) {
        seedWorkspaceFeatureFlagsUseCase.seedDefaults(event.workspaceId());
    }

    @EventListener
    public void onWorkspaceDeleted(WorkspaceDeletedEvent event) {
        deleteWorkspaceFeatureFlagsUseCase.deleteForWorkspace(event.workspaceId());
    }
}
