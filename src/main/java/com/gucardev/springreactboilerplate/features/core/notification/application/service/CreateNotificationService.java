package com.gucardev.springreactboilerplate.features.core.notification.application.service;

import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.CreateNotificationCommand;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.CreateNotificationUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.out.FeatureFlagCheckPort;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.out.SaveNotificationPort;
import com.gucardev.springreactboilerplate.features.core.notification.domain.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates in-app notifications, gated by the per-workspace {@code IN_APP_NOTIFICATIONS} feature flag
 * (checked via the {@link FeatureFlagCheckPort} output port): when the flag is off for a workspace,
 * notifications are silently dropped. Reusable by any feature (directly or via {@code NotificationEvent}).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateNotificationService implements CreateNotificationUseCase {

    private final SaveNotificationPort saveNotificationPort;
    private final FeatureFlagCheckPort featureFlagCheckPort;

    @Override
    @Transactional
    public void create(CreateNotificationCommand command) {
        if (!featureFlagCheckPort.isInAppNotificationsEnabled(command.workspaceId())) {
            log.debug("Notifications disabled for workspace {} — dropping '{}'",
                    command.workspaceId(), command.type());
            return;
        }
        saveNotificationPort.save(Notification.builder()
                .workspaceId(command.workspaceId())
                .recipientId(command.recipientId())
                .type(command.type())
                .title(command.title())
                .message(command.message())
                .read(false)
                .build());
    }
}
