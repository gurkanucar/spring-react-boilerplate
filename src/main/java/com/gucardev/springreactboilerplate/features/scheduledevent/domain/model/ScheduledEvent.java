package com.gucardev.springreactboilerplate.features.scheduledevent.domain.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The scheduled-event aggregate — the pure domain model at the centre of the hexagon.
 *
 * <p>It carries no JPA, Spring or serialization annotations: the application core depends on this
 * class, never on the persistence entity or the web DTO. Driven adapters map to/from it
 * ({@code ScheduledEventJpaEntity} on the way out, {@code ScheduledEventResponse} on the way in to
 * the client).
 *
 * <p>The row is the source of truth for the event's lifecycle: it is created {@link
 * ScheduledEventStatus#SCHEDULED} before the delayed message is published; the consumer flips it to
 * {@link #markDelivered() DELIVERED} / {@link #markFailed(String) FAILED} when the broker releases
 * the message, and a {@link #cancel()} flips it to {@code CANCELLED} (the in-flight message is then
 * skipped on arrival — the delayed-message plugin can't recall an already-published message).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledEvent {

    /** Last handler error is capped to this length so a huge stack trace can't blow the column. */
    private static final int MAX_ERROR_LENGTH = 1000;

    private UUID id;
    private String eventType;
    private Map<String, Object> payload;
    private ScheduledEventStatus status;
    private Long delaySeconds;
    private Instant scheduledAt;
    private Instant fireAt;
    private Instant deliveredAt;
    private Instant cancelledAt;
    private Integer attempts;
    private String lastError;

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /** Only a still-pending (SCHEDULED) event can be cancelled. */
    public boolean isCancellable() {
        return this.status == ScheduledEventStatus.SCHEDULED;
    }

    public boolean isCancelled() {
        return this.status == ScheduledEventStatus.CANCELLED;
    }

    public boolean isDelivered() {
        return this.status == ScheduledEventStatus.DELIVERED;
    }

    /** Domain transition: tombstone a pending event so the listener skips it when the broker delivers. */
    public void cancel() {
        this.status = ScheduledEventStatus.CANCELLED;
        this.cancelledAt = Instant.now();
    }

    /** Records another delivery attempt; lets handlers stay idempotent. */
    public void incrementAttempts() {
        this.attempts = (this.attempts == null ? 0 : this.attempts) + 1;
    }

    /** Domain transition: the consumer handled the event successfully. */
    public void markDelivered() {
        this.status = ScheduledEventStatus.DELIVERED;
        this.deliveredAt = Instant.now();
        this.lastError = null;
    }

    /** Domain transition: the handler threw; keep the (truncated) error for diagnostics. */
    public void markFailed(String error) {
        this.status = ScheduledEventStatus.FAILED;
        this.lastError = truncate(error);
    }

    private static String truncate(String message) {
        if (message == null) {
            return null;
        }
        return message.length() <= MAX_ERROR_LENGTH ? message : message.substring(0, MAX_ERROR_LENGTH);
    }
}
