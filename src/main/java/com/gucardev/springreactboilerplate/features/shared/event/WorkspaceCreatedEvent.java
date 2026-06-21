package com.gucardev.springreactboilerplate.features.shared.event;

import java.util.UUID;

/**
 * Published when a workspace has been created. Lives in {@code shared} so any feature can react to a
 * workspace lifecycle change without depending on the workspace feature directly (and vice versa).
 * Handled synchronously within the creating transaction.
 */
public record WorkspaceCreatedEvent(UUID workspaceId) {
}
