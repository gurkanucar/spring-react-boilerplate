package com.gucardev.springreactboilerplate.features.scheduledevent.entity;

import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

/**
 * Durable record of an event scheduled for delayed delivery via RabbitMQ. The row is the source of
 * truth for the event's lifecycle: it is written {@code SCHEDULED} before the delayed message is
 * published, the listener flips it to {@code DELIVERED}/{@code FAILED} when the broker releases the
 * message, and a cancel flips it to {@code CANCELLED} (the in-flight message is then skipped on
 * arrival — the delayed-message plugin can't recall an already-published message).
 *
 * <p>Indexes target the queries this table actually serves: by {@code status}, by {@code fire_at}
 * (overdue/upcoming scans), and the composite {@code (status, fire_at)} for a reconciliation sweep
 * that finds events still {@code SCHEDULED} past their fire time (e.g. messages lost to a broker
 * wipe). {@code created_at} mirrors the convention on the other tables for time-ordered listing.
 */
@Entity
@Table(name = "scheduled_events",
        indexes = {
                @Index(name = "idx_scheduled_events_status", columnList = "status"),
                @Index(name = "idx_scheduled_events_event_type", columnList = "event_type"),
                @Index(name = "idx_scheduled_events_fire_at", columnList = "fire_at"),
                @Index(name = "idx_scheduled_events_status_fire_at", columnList = "status, fire_at"),
                @Index(name = "idx_scheduled_events_created_at", columnList = "created_at")
        })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledEvent extends BaseEntity {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    /**
     * Discriminator telling the consumer how to interpret {@link #payload} (which keys to expect,
     * what to cast values to). The listener branches on this — see {@code ProcessScheduledEventUseCase}.
     */
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    /**
     * Arbitrary structured payload acted on when the event fires. Stored as PostgreSQL {@code jsonb}
     * via {@link SqlTypes#JSON} — Hibernate serializes the map to/from JSON, so you can put any
     * shape here and (if needed) query into it with PG's {@code ->>}/{@code @>} operators. Nullable:
     * a type-only event (one whose {@code eventType} fully describes the action) needs no payload.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ScheduledEventStatus status;

    /** Configured delay between {@link #scheduledAt} and {@link #fireAt}, in seconds. */
    @Column(name = "delay_seconds", nullable = false)
    private Long delaySeconds;

    /** When the event was accepted/published. */
    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    /** When the broker is expected to release it ({@code scheduledAt + delaySeconds}). */
    @Column(name = "fire_at", nullable = false)
    private Instant fireAt;

    /** When the listener actually handled it (null until delivered). */
    @Column(name = "delivered_at")
    private Instant deliveredAt;

    /** When it was cancelled (null unless cancelled). */
    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    /** How many times the listener has attempted delivery; lets handlers stay idempotent. */
    @Column(nullable = false)
    @Builder.Default
    private Integer attempts = 0;

    /** Last handler error, when {@link #status} is {@code FAILED}. */
    @Column(name = "last_error", length = 1000)
    private String lastError;
}
