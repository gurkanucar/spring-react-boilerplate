package com.gucardev.springreactboilerplate.features.outbox.entity;

/**
 * Lifecycle of an {@link OutboxMessage}.
 *
 * <pre>
 *   PENDING ──(relay publishes to broker)──▶ PUBLISHED
 *      │
 *      └──(publish kept failing past max attempts)──▶ FAILED  (dead-lettered; needs manual attention)
 * </pre>
 *
 * <p>A transient publish failure leaves the row {@code PENDING} with a future {@code nextAttemptAt}
 * so the relay retries it later; only exhausting the attempt budget moves it to {@code FAILED}.
 */
public enum OutboxStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
