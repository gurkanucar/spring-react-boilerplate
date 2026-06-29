package com.gucardev.springreactboilerplate.features.core.featureflag.application.port.out;

import java.util.UUID;

/**
 * Output port: remove every feature-flag override for a workspace — used when the workspace is deleted.
 */
public interface DeleteFeatureFlagPort {

    int deleteByWorkspaceId(UUID workspaceId);
}
