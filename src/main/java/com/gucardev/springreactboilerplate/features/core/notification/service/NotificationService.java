package com.gucardev.springreactboilerplate.features.core.notification.service;

import com.gucardev.springreactboilerplate.features.core.featureflag.FeatureFlags;
import com.gucardev.springreactboilerplate.features.core.featureflag.service.FeatureFlagService;
import com.gucardev.springreactboilerplate.features.core.notification.entity.Notification;
import com.gucardev.springreactboilerplate.features.core.notification.repository.NotificationRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates in-app notifications, gated by the per-workspace {@code IN_APP_NOTIFICATIONS} feature flag:
 * when the flag is off for a workspace, notifications are silently dropped. Reusable by any feature
 * (directly or via {@code NotificationEvent}).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final FeatureFlagService featureFlagService;

    @Transactional
    public void create(UUID workspaceId, UUID recipientId, String type, String title, String message) {
        if (!featureFlagService.isEnabled(workspaceId, FeatureFlags.IN_APP_NOTIFICATIONS)) {
            log.debug("Notifications disabled for workspace {} — dropping '{}'", workspaceId, type);
            return;
        }
        repository.save(Notification.builder()
                .workspaceId(workspaceId)
                .recipientId(recipientId)
                .type(type)
                .title(title)
                .message(message)
                .read(false)
                .build());
    }
}
