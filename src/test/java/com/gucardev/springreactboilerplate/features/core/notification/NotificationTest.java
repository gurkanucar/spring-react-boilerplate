package com.gucardev.springreactboilerplate.features.core.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.gucardev.springreactboilerplate.features.core.featureflag.FeatureFlags;
import com.gucardev.springreactboilerplate.features.core.featureflag.service.FeatureFlagService;
import com.gucardev.springreactboilerplate.features.core.notification.repository.NotificationRepository;
import com.gucardev.springreactboilerplate.features.core.notification.service.NotificationService;
import com.gucardev.springreactboilerplate.features.shared.event.NotificationEvent;
import com.gucardev.springreactboilerplate.features.shared.event.WorkspaceDeletedEvent;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Covers the notification flow at the service/event level: feature-flag gating, event-driven
 * creation, unread counting and workspace-deletion cleanup. The current-user/controller layer is
 * thin and mirrors the proven workspace-scoped pattern.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationTest {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository repository;
    @Autowired
    private FeatureFlagService featureFlagService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Test
    void create_isDroppedWhenFlagOff() {
        UUID workspace = UUID.randomUUID();
        UUID user = UUID.randomUUID();

        notificationService.create(workspace, user, "X", "t", "m"); // flag off (no override)

        assertThat(repository.countByWorkspaceIdAndRecipientIdAndReadFalse(workspace, user)).isZero();
    }

    @Test
    void create_persistsWhenFlagOn() {
        UUID workspace = UUID.randomUUID();
        UUID user = UUID.randomUUID();
        featureFlagService.set(workspace, FeatureFlags.IN_APP_NOTIFICATIONS, true);

        notificationService.create(workspace, user, "NEWS_CREATED", "News published", "Hello");

        assertThat(repository.countByWorkspaceIdAndRecipientIdAndReadFalse(workspace, user)).isEqualTo(1);
    }

    @Test
    void notificationEvent_feedsTheFeed() {
        UUID workspace = UUID.randomUUID();
        UUID user = UUID.randomUUID();
        featureFlagService.set(workspace, FeatureFlags.IN_APP_NOTIFICATIONS, true);

        eventPublisher.publishEvent(new NotificationEvent(workspace, user, "X", "t", "m"));

        assertThat(repository.countByWorkspaceIdAndRecipientIdAndReadFalse(workspace, user)).isEqualTo(1);
    }

    @Test
    void workspaceDeleted_clearsNotifications() {
        UUID workspace = UUID.randomUUID();
        UUID user = UUID.randomUUID();
        featureFlagService.set(workspace, FeatureFlags.IN_APP_NOTIFICATIONS, true);
        notificationService.create(workspace, user, "X", "t", "m");

        eventPublisher.publishEvent(new WorkspaceDeletedEvent(workspace));

        assertThat(repository.countByWorkspaceIdAndRecipientIdAndReadFalse(workspace, user)).isZero();
    }
}
