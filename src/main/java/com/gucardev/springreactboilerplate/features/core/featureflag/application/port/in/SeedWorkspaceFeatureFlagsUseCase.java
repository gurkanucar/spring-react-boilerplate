package com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in;

import java.util.UUID;

/**
 * Input port: enable every known flag for a workspace — used when a workspace is first created.
 * Invoked by the workspace-lifecycle driving adapter.
 */
public interface SeedWorkspaceFeatureFlagsUseCase {

    void seedDefaults(UUID workspaceId);
}
