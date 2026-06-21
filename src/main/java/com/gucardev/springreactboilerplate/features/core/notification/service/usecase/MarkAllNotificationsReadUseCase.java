package com.gucardev.springreactboilerplate.features.core.notification.service.usecase;

import com.gucardev.springreactboilerplate.features.core.notification.repository.NotificationRepository;
import com.gucardev.springreactboilerplate.infra.config.security.SecurityUtils;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarkAllNotificationsReadUseCase {

    private final NotificationRepository repository;

    /**
     * Marks all of the current user's unread notifications read; returns how many were updated.
     * Rate limited via resilience4j's built-in {@code @RateLimiter} (configured under
     * {@code resilience4j.ratelimiter.instances.markAllNotificationsRead}). Note this is a
     * <strong>name-based, global</strong> limit shared across callers, not per-user.
     */
    @RateLimiter(name = "markAllNotificationsRead")
    @Transactional
    public int execute() {
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        UUID userId = SecurityUtils.requireCurrentUserId();
        return repository.markAllRead(workspaceId, userId, LocalDateTime.now());
    }
}
