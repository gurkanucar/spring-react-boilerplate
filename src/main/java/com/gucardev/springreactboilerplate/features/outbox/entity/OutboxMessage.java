package com.gucardev.springreactboilerplate.features.outbox.entity;

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
 * A single event the application intends to publish, written to the database in the SAME transaction
 * as the business change that produced it (see {@code OutboxEventRecorder}). This is the heart of the
 * transactional outbox: the business write and the "please publish this" intent commit atomically, so
 * a crash can never leave one without the other.
 *
 * <p>The row is the source of truth for delivery. The relay ({@code OutboxRelayJob} →
 * {@code OutboxDispatcher} → {@code OutboxMessageSender}) polls {@code PENDING} rows, publishes each to
 * the broker, and flips it to {@code PUBLISHED}. A failed publish bumps {@link #attempts}, records
 * {@link #lastError} and pushes {@link #nextAttemptAt} out (backoff); exhausting the attempt budget
 * flips it to {@code FAILED}.
 *
 * <p>{@link #id} doubles as the event id carried in the message envelope, which the consumer uses for
 * idempotent dedup (see {@code ProcessedMessage}).
 */
@Entity
@Table(name = "outbox_messages",
        indexes = {
                @Index(name = "idx_outbox_status_next_attempt", columnList = "status, next_attempt_at"),
                @Index(name = "idx_outbox_status", columnList = "status"),
                @Index(name = "idx_outbox_created_at", columnList = "created_at")
        })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxMessage extends BaseEntity {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    /** The kind of aggregate this event is about (e.g. {@code "Order"}). Diagnostic / routing aid. */
    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    /** Identifier of the aggregate instance (e.g. the order id). Lets you trace events per aggregate. */
    @Column(name = "aggregate_id", nullable = false, length = 100)
    private String aggregateId;

    /** Discriminator the consumer branches on to interpret {@link #payload} (e.g. {@code "OrderCreated"}). */
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    /** Routing key the relay publishes this row under on the shared outbox exchange. */
    @Column(name = "routing_key", nullable = false, length = 150)
    private String routingKey;

    /**
     * Structured event body, stored as PostgreSQL {@code jsonb} (Hibernate serializes the map
     * to/from JSON). The consumer reads keys out of it after receiving the envelope.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status;

    /** Publish attempts so far; once this hits the configured max the row is marked {@code FAILED}. */
    @Column(nullable = false)
    @Builder.Default
    private Integer attempts = 0;

    /** Last publish error, for diagnostics. */
    @Column(name = "last_error", length = 1000)
    private String lastError;

    /**
     * Earliest time the relay may (re)try publishing this row. Set to "now" on insert and pushed into
     * the future after a failed attempt (backoff), so a flaky broker doesn't get hammered every poll.
     */
    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    /** When the relay successfully handed the message to the broker (null until {@code PUBLISHED}). */
    @Column(name = "published_at")
    private Instant publishedAt;
}
