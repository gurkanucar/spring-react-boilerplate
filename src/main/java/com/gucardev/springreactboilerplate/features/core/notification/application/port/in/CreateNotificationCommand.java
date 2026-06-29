package com.gucardev.springreactboilerplate.features.core.notification.application.port.in;

import java.util.UUID;

/**
 * Driving-side command for creating an in-app notification. Carries already-validated input from a
 * driving adapter (e.g. the event listener) into the application core.
 */
public record CreateNotificationCommand(
        UUID workspaceId,
        UUID recipientId,
        String type,
        String title,
        String message
) {
}
