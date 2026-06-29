package com.gucardev.springreactboilerplate.features.core.notification.application.port.in;

import java.util.UUID;

/**
 * Input port: remove every notification for a workspace — used when the workspace is deleted.
 */
public interface DeleteWorkspaceNotificationsUseCase {

    void deleteForWorkspace(UUID workspaceId);
}
