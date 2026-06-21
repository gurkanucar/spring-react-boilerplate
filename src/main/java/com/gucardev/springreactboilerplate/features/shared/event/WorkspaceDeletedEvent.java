package com.gucardev.springreactboilerplate.features.shared.event;

import java.util.UUID;

/**
 * Published when a workspace has been deleted. Lives in {@code shared} so any feature can clean up
 * its workspace-scoped data without depending on the workspace feature directly. Handled
 * synchronously within the deleting transaction.
 */
public record WorkspaceDeletedEvent(UUID workspaceId) {
}
