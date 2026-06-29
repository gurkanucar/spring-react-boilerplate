package com.gucardev.springreactboilerplate.features.core.notification.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.notification.application.port.out.DeleteNotificationPort;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.out.LoadNotificationPort;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.out.MarkAllNotificationsReadPort;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.out.SaveNotificationPort;
import com.gucardev.springreactboilerplate.features.core.notification.domain.model.Notification;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the notification output ports with Spring Data JPA. Maps domain ⇄ entity at
 * the boundary so the application core stays persistence-agnostic.
 */
@Component
@RequiredArgsConstructor
public class NotificationPersistenceAdapter
        implements LoadNotificationPort, SaveNotificationPort, MarkAllNotificationsReadPort, DeleteNotificationPort {

    private final NotificationJpaRepository repository;
    private final NotificationPersistenceMapper mapper;

    @Override
    public Optional<Notification> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Notification> findByRecipient(UUID workspaceId, UUID recipientId, Pageable pageable) {
        return repository.findByWorkspaceIdAndRecipientId(workspaceId, recipientId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Notification> findUnreadByRecipient(UUID workspaceId, UUID recipientId, Pageable pageable) {
        return repository.findByWorkspaceIdAndRecipientIdAndReadFalse(workspaceId, recipientId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public long countUnread(UUID workspaceId, UUID recipientId) {
        return repository.countByWorkspaceIdAndRecipientIdAndReadFalse(workspaceId, recipientId);
    }

    @Override
    public Notification save(Notification notification) {
        return mapper.toDomain(repository.save(mapper.toEntity(notification)));
    }

    @Override
    public int markAllRead(UUID workspaceId, UUID recipientId, LocalDateTime now) {
        return repository.markAllRead(workspaceId, recipientId, now);
    }

    @Override
    public int deleteByWorkspaceId(UUID workspaceId) {
        return repository.deleteByWorkspaceId(workspaceId);
    }
}
