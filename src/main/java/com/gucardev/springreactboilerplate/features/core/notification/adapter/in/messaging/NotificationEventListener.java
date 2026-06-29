package com.gucardev.springreactboilerplate.features.core.notification.adapter.in.messaging;

import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.CreateNotificationCommand;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.CreateNotificationUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.DeleteWorkspaceNotificationsUseCase;
import com.gucardev.springreactboilerplate.features.shared.event.NotificationEvent;
import com.gucardev.springreactboilerplate.features.shared.event.WorkspaceDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Driving (messaging-in) adapter: turns domain events into notifications. Any {@link NotificationEvent}
 * is persisted (subject to the workspace's feature flag), and a deleted workspace has its notifications
 * cleaned up. Synchronous, so creation joins the publisher's transaction.
 *
 * <p>The listener stays thin: it translates the Spring application event into a command and delegates
 * to the transactional input ports.
 */
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final CreateNotificationUseCase createNotificationUseCase;
    private final DeleteWorkspaceNotificationsUseCase deleteWorkspaceNotificationsUseCase;

    @EventListener
    public void onNotification(NotificationEvent event) {
        createNotificationUseCase.create(new CreateNotificationCommand(
                event.workspaceId(), event.recipientId(),
                event.type(), event.title(), event.message()));
    }

    @EventListener
    public void onWorkspaceDeleted(WorkspaceDeletedEvent event) {
        deleteWorkspaceNotificationsUseCase.deleteForWorkspace(event.workspaceId());
    }
}
