package com.gucardev.springreactboilerplate.features.core.notification.application.service;

import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.GetMyNotificationsUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.NotificationQuery;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.out.LoadNotificationPort;
import com.gucardev.springreactboilerplate.features.core.notification.domain.model.Notification;
import com.gucardev.springreactboilerplate.infra.config.security.SecurityUtils;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lists the current user's notifications in the active workspace (paged), optionally unread-only.
 */
@Service
@RequiredArgsConstructor
public class GetMyNotificationsService implements GetMyNotificationsUseCase {

    private final LoadNotificationPort loadNotificationPort;

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getMine(NotificationQuery query) {
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        UUID userId = SecurityUtils.requireCurrentUserId();
        return Boolean.TRUE.equals(query.unreadOnly())
                ? loadNotificationPort.findUnreadByRecipient(workspaceId, userId, query.pageable())
                : loadNotificationPort.findByRecipient(workspaceId, userId, query.pageable());
    }
}
