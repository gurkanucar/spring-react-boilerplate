package com.gucardev.springreactboilerplate.features.core.notification.service.usecase;

import com.gucardev.springreactboilerplate.features.core.notification.entity.Notification;
import com.gucardev.springreactboilerplate.features.core.notification.exception.NotificationExceptionType;
import com.gucardev.springreactboilerplate.features.core.notification.repository.NotificationRepository;
import com.gucardev.springreactboilerplate.infra.config.security.SecurityUtils;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationFinder {

    private final NotificationRepository repository;

    /** Loads a notification owned by the current user in the active workspace; else reports NOT_FOUND. */
    public Notification findOwn(UUID id) {
        Notification notification = repository.findById(id)
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
