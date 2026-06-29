package com.gucardev.springreactboilerplate.features.core.user.application.port.out;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port: resolve a workspace's owning organization id (empty when the workspace is absent).
 * Backed by a driven adapter that delegates to the workspace feature, keeping the user core off the
 * workspace internals.
 */
public interface WorkspaceLookupPort {

    Optional<UUID> findOrganizationId(UUID workspaceId);
}
