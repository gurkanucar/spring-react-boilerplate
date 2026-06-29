package com.gucardev.springreactboilerplate.features.core.notification.application.port.out;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Output port: bulk-mark a user's unread notifications read in one statement; returns how many rows
 * were updated. Implemented by a driven persistence adapter.
 */
public interface MarkAllNotificationsReadPort {

    int markAllRead(UUID workspaceId, UUID recipientId, LocalDateTime now);
}
