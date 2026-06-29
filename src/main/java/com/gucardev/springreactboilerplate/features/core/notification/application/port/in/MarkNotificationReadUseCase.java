package com.gucardev.springreactboilerplate.features.core.notification.application.port.in;

import com.gucardev.springreactboilerplate.features.core.notification.domain.model.Notification;
import java.util.UUID;

/**
 * Input port: mark a single notification (owned by the current user) read.
 */
public interface MarkNotificationReadUseCase {

    Notification markRead(UUID id);
}
