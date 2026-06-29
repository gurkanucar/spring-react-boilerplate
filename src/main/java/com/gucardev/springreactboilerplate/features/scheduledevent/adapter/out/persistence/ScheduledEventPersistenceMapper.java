package com.gucardev.springreactboilerplate.features.scheduledevent.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link ScheduledEvent} domain model and the {@link ScheduledEventJpaEntity}.
 * Kept hand-written (not MapStruct) because it spans the audit fields on {@code BaseEntity} via the
 * super-builder and is trivial enough to read at a glance.
 */
@Component
public class ScheduledEventPersistenceMapper {

    ScheduledEvent toDomain(ScheduledEventJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ScheduledEvent.builder()
                .id(entity.getId())
                .eventType(entity.getEventType())
                .payload(entity.getPayload())
                .status(entity.getStatus())
                .delaySeconds(entity.getDelaySeconds())
                .scheduledAt(entity.getScheduledAt())
                .fireAt(entity.getFireAt())
                .deliveredAt(entity.getDeliveredAt())
                .cancelledAt(entity.getCancelledAt())
                .attempts(entity.getAttempts())
                .lastError(entity.getLastError())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    ScheduledEventJpaEntity toEntity(ScheduledEvent event) {
        if (event == null) {
            return null;
        }
        return ScheduledEventJpaEntity.builder()
                .id(event.getId())
                .eventType(event.getEventType())
                .payload(event.getPayload())
                .status(event.getStatus())
                .delaySeconds(event.getDelaySeconds())
                .scheduledAt(event.getScheduledAt())
                .fireAt(event.getFireAt())
                .deliveredAt(event.getDeliveredAt())
                .cancelledAt(event.getCancelledAt())
                .attempts(event.getAttempts())
                .lastError(event.getLastError())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .createdBy(event.getCreatedBy())
                .updatedBy(event.getUpdatedBy())
                .build();
    }
}
