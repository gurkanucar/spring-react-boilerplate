package com.gucardev.springreactboilerplate.features.core.notification.listener;

import com.gucardev.springreactboilerplate.features.core.notification.repository.NotificationRepository;
import com.gucardev.springreactboilerplate.features.core.notification.service.NotificationService;
import com.gucardev.springreactboilerplate.features.shared.event.NotificationEvent;
import com.gucardev.springreactboilerplate.features.shared.event.WorkspaceDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Turns domain events into notifications: any {@link NotificationEvent} is persisted (subject to the
 * workspace's feature flag), and a deleted workspace has its notifications cleaned up. Synchronous,
 * so creation joins the publisher's transaction.
 */
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final NotificationRepository repository;

    @EventListener
    public void onNotification(NotificationEvent event) {
        notificationService.create(event.workspaceId(), event.recipientId(),
                event.type(), event.title(), event.message());
    }

    @EventListener
    @Transactional
    public void onWorkspaceDeleted(WorkspaceDeletedEvent event) {
        repository.deleteByWorkspaceId(event.workspaceId());
    }
}
