package com.gucardev.springreactboilerplate.features.scheduledevent.domain.message;

import java.time.Instant;

/**
 * The body carried over RabbitMQ for a scheduled event — the event contract shared by the two
 * messaging adapters: the driven (messaging-out) publisher writes it, the driving (messaging-in)
 * listener reads it. The {@code ScheduledEvent} row is the source of truth (it holds the payload +
 * status), so the message only needs to carry the id to look it up. {@code eventType} rides along for
 * early routing/logging, and {@code scheduledAt}/{@code fireAt} for logging the delivery lag.
 * Travels as JSON.
 *
 * @param id          the {@code ScheduledEvent} id to process when the broker releases this message
 * @param eventType   discriminator (same value as the row) — handy for routing/logging before load
 * @param scheduledAt when the event was published
 * @param fireAt      the instant the broker should release the message (scheduledAt + delay)
 */
public record ScheduledEventMessage(
        String id,
        String eventType,
        Instant scheduledAt,
        Instant fireAt
) {
}
