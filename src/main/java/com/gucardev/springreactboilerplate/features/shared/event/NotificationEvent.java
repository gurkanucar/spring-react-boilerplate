package com.gucardev.springreactboilerplate.features.shared.event;

import java.util.UUID;

/**
 * Fire-and-forget request to deliver an in-app notification to a user in a workspace. Any feature
 * can publish this; the notification module listens, applies the per-workspace feature-flag gate,
 * and persists it. Lives in {@code shared} so publishers don't depend on the notification feature.
 */
public record NotificationEvent(
        UUID workspaceId,
        UUID recipientId,
        String type,
        String title,
        String message
) {
}
