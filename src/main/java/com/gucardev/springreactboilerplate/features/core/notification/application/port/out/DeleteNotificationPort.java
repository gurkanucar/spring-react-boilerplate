package com.gucardev.springreactboilerplate.features.core.notification.application.port.out;

import java.util.UUID;

/**
 * Output port: remove every notification for a workspace; returns how many rows were deleted.
 * Implemented by a driven persistence adapter.
 */
public interface DeleteNotificationPort {

    int deleteByWorkspaceId(UUID workspaceId);
}
