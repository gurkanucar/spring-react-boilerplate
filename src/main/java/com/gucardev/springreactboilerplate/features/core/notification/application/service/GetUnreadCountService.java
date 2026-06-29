package com.gucardev.springreactboilerplate.features.core.notification.application.service;

import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.GetUnreadCountUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.out.LoadNotificationPort;
import com.gucardev.springreactboilerplate.infra.config.security.SecurityUtils;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Counts the current user's unread notifications in the active workspace.
 */
@Service
@RequiredArgsConstructor
public class GetUnreadCountService implements GetUnreadCountUseCase {

    private final LoadNotificationPort loadNotificationPort;

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        UUID userId = SecurityUtils.requireCurrentUserId();
        return loadNotificationPort.countUnread(workspaceId, userId);
    }
}
