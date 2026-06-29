package com.gucardev.springreactboilerplate.features.core.notification.application.port.out;

import com.gucardev.springreactboilerplate.features.core.notification.domain.model.Notification;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Output port: read notifications from the store. Implemented by a driven persistence adapter.
 */
public interface LoadNotificationPort {

    Optional<Notification> findById(UUID id);

    Page<Notification> findByRecipient(UUID workspaceId, UUID recipientId, Pageable pageable);

    Page<Notification> findUnreadByRecipient(UUID workspaceId, UUID recipientId, Pageable pageable);

    long countUnread(UUID workspaceId, UUID recipientId);
}
