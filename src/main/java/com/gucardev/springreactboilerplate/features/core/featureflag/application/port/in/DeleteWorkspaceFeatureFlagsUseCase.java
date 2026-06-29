package com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in;

import java.util.UUID;

/**
 * Input port: remove every flag override for a workspace — used when the workspace is deleted.
 * Invoked by the workspace-lifecycle driving adapter.
 */
public interface DeleteWorkspaceFeatureFlagsUseCase {

    void deleteForWorkspace(UUID workspaceId);
}
