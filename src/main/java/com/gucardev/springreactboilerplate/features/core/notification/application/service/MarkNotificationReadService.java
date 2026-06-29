package com.gucardev.springreactboilerplate.features.core.notification.application.service;

import com.gucardev.springreactboilerplate.features.core.notification.application.port.in.MarkNotificationReadUseCase;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.out.SaveNotificationPort;
import com.gucardev.springreactboilerplate.features.core.notification.domain.model.Notification;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Marks a single notification (owned by the current user) read, stamping the read time. Persists only
 * when the notification was previously unread.
 */
@Service
@RequiredArgsConstructor
public class MarkNotificationReadService implements MarkNotificationReadUseCase {

    private final NotificationFinder finder;
    private final SaveNotificationPort saveNotificationPort;

    @Override
    @Transactional
    public Notification markRead(UUID id) {
        Notification notification = finder.findOwn(id);
        if (!Boolean.TRUE.equals(notification.getRead())) {
            notification.markRead();
            notification = saveNotificationPort.save(notification);
        }
        return notification;
    }
}
