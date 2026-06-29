package com.gucardev.springreactboilerplate.features.outbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * The consumer-side "inbox" / idempotency ledger. One row per event a consumer has fully processed.
 *
 * <p>Because delivery is at-least-once (the relay may re-publish after a crash, and the broker may
 * redeliver), a consumer must be idempotent. Before doing its side effect it checks for a row here
 * keyed by the event id; if present, the event was already handled and is skipped. The marker row is
 * written in the SAME transaction as the side effect, so either both commit or neither does.
 *
 * <p>{@link #id} is the event id (the {@code OutboxMessage} id carried in the envelope). The PK is the
 * event id alone, which assumes one logical consumer per event — fine for this demo. For fan-out to
 * several independent consumers, switch to a composite key of (event id, {@link #consumer}).
 */
@Entity
@Table(name = "processed_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedMessage {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    /** Which consumer processed it (informational; see class note on multi-consumer fan-out). */
    @Column(nullable = false, length = 150)
    private String consumer;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
}
