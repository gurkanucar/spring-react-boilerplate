package com.gucardev.springreactboilerplate.features.core.notification.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The notification aggregate — the pure domain model at the centre of the hexagon.
 *
 * <p>It carries no JPA, Spring or serialization annotations: the application core depends on this
 * class, never on the persistence entity or the web DTO. Driven adapters map to/from it
 * ({@code NotificationJpaEntity} on the way out, {@code NotificationResponse} on the way in to the
 * client).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    private UUID id;
    private UUID workspaceId;

    /** The user this notification is for. */
    private UUID recipientId;

    /** Machine-readable category, e.g. NEWS_CREATED. */
    private String type;
    private String title;
    private String message;
    private Boolean read;
    private LocalDateTime readAt;

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /** Domain transition: mark the notification read, stamping the read time, if not already read. */
    public void markRead() {
        if (!Boolean.TRUE.equals(this.read)) {
            this.read = true;
            this.readAt = LocalDateTime.now();
        }
    }
}
