package com.gucardev.springreactboilerplate.features.core.notification.application.service;

import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.DeleteWorkspaceNotificationsUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.out.DeleteNotificationPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Removes every notification for a workspace — used when the workspace is deleted.
 */
@Service
@RequiredArgsConstructor
public class DeleteWorkspaceNotificationsService implements DeleteWorkspaceNotificationsUseCase {

    private final DeleteNotificationPort deleteNotificationPort;

    @Override
    @Transactional
    public void deleteForWorkspace(UUID workspaceId) {
        deleteNotificationPort.deleteByWorkspaceId(workspaceId);
    }
}
