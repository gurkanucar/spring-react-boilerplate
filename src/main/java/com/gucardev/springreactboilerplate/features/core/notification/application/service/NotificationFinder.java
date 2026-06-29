package com.gucardev.springreactboilerplate.features.core.notification.application.service;

import com.gucardev.springreactboilerplate.features.core.notification.application.exception.NotificationExceptionType;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.out.LoadNotificationPort;
import com.gucardev.springreactboilerplate.features.core.notification.domain.model.Notification;
import com.gucardev.springreactboilerplate.infra.config.security.SecurityUtils;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared "fetch or 404" lookup for notifications owned by the current user in the active workspace.
 */
@Service
@RequiredArgsConstructor
public class NotificationFinder {

    private final LoadNotificationPort loadNotificationPort;

    /** Loads a notification owned by the current user in the active workspace; else reports NOT_FOUND. */
    public Notification findOwn(UUID id) {
        Notification notification = loadNotificationPort.findById(id)
                .orElseThrow(() -> NotificationExceptionType.NOT_FOUND.toException(id));
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        UUID userId = SecurityUtils.requireCurrentUserId();
        if (!notification.getWorkspaceId().equals(workspaceId)
                || !notification.getRecipientId().equals(userId)) {
            throw NotificationExceptionType.NOT_FOUND.toException(id);
        }
        return notification;
    }
}
