package com.gucardev.springreactboilerplate.features.core.notification.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.notification.domain.model.Notification;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link Notification} domain model and the {@link NotificationJpaEntity}. Kept
 * hand-written (not MapStruct) because it spans the audit fields on {@code BaseEntity} via the
 * super-builder and is trivial enough to read at a glance.
 */
@Component
public class NotificationPersistenceMapper {

    Notification toDomain(NotificationJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Notification.builder()
                .id(entity.getId())
                .workspaceId(entity.getWorkspaceId())
                .recipientId(entity.getRecipientId())
                .type(entity.getType())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .read(entity.getRead())
                .readAt(entity.getReadAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    NotificationJpaEntity toEntity(Notification notification) {
        if (notification == null) {
            return null;
        }
        return NotificationJpaEntity.builder()
                .id(notification.getId())
                .workspaceId(notification.getWorkspaceId())
                .recipientId(notification.getRecipientId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.getRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .createdBy(notification.getCreatedBy())
                .updatedBy(notification.getUpdatedBy())
                .build();
    }
}
