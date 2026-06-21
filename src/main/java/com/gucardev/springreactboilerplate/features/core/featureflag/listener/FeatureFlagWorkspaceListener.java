package com.gucardev.springreactboilerplate.features.core.featureflag.listener;

import com.gucardev.springreactboilerplate.features.core.featureflag.service.FeatureFlagService;
import com.gucardev.springreactboilerplate.features.shared.event.WorkspaceCreatedEvent;
import com.gucardev.springreactboilerplate.features.shared.event.WorkspaceDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Keeps per-workspace feature flags in sync with the workspace lifecycle: seeds the catalog defaults
 * when a workspace is created and removes the overrides when it is deleted. Listeners are synchronous,
 * so they run inside the workspace transaction (seeding rolls back with the workspace if it fails).
 */
@Component
@RequiredArgsConstructor
public class FeatureFlagWorkspaceListener {

    private final FeatureFlagService featureFlagService;

    @EventListener
    public void onWorkspaceCreated(WorkspaceCreatedEvent event) {
        featureFlagService.enableDefaults(event.workspaceId());
    }

    @EventListener
    public void onWorkspaceDeleted(WorkspaceDeletedEvent event) {
        featureFlagService.deleteForWorkspace(event.workspaceId());
    }
}
