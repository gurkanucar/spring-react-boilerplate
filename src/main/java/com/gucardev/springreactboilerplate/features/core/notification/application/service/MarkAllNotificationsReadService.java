package com.gucardev.springreactboilerplate.features.core.notification.application.service;

import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.MarkAllNotificationsReadUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.out.MarkAllNotificationsReadPort;
import com.gucardev.springreactboilerplate.infra.config.security.SecurityUtils;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Marks all of the current user's unread notifications read; returns how many were updated.
 * Rate limited via resilience4j's built-in {@code @RateLimiter} (configured under
 * {@code resilience4j.ratelimiter.instances.markAllNotificationsRead}). Note this is a
 * <strong>name-based, global</strong> limit shared across callers, not per-user.
 */
@Service
@RequiredArgsConstructor
public class MarkAllNotificationsReadService implements MarkAllNotificationsReadUseCase {

    private final MarkAllNotificationsReadPort markAllNotificationsReadPort;

    @Override
    @RateLimiter(name = "markAllNotificationsRead")
    @Transactional
    public int markAllRead() {
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        UUID userId = SecurityUtils.requireCurrentUserId();
        return markAllNotificationsReadPort.markAllRead(workspaceId, userId, LocalDateTime.now());
    }
}
