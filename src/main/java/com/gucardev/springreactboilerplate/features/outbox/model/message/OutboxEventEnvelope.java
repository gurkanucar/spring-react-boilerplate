package com.gucardev.springreactboilerplate.features.outbox.model.message;

import java.time.Instant;
import java.util.Map;

/**
 * The body the relay puts on the wire for an outbox row. Travels as JSON (via the
 * {@code JacksonJsonMessageConverter} bean). Generic on purpose — the relay doesn't know or care what
 * an event means; the consumer branches on {@link #eventType} and reads {@link #payload}.
 *
 * <p>{@link #eventId} is the {@code OutboxMessage} id; the consumer records it in the inbox
 * ({@code processed_messages}) to dedupe redeliveries, so it is the idempotency key for the whole
 * pipeline. It is also stamped onto the AMQP {@code messageId} property for broker-level tracing.
 *
 * @param eventId       outbox row id; the idempotency key the consumer dedupes on
 * @param aggregateType kind of aggregate (e.g. {@code "Order"})
 * @param aggregateId   aggregate instance id (e.g. the order id)
 * @param eventType     discriminator the consumer branches on (e.g. {@code "OrderCreated"})
 * @param payload       structured event body
 * @param occurredAt    when the event was recorded into the outbox
 */
public record OutboxEventEnvelope(
        String eventId,
        String aggregateType,
        String aggregateId,
        String eventType,
        Map<String, Object> payload,
        Instant occurredAt
) {
}
