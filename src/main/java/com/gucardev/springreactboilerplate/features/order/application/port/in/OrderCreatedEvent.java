package com.gucardev.springreactboilerplate.features.order.application.port.in;

import java.util.UUID;

/**
 * Driving-side representation of a consumed {@code OrderCreated} event. The messaging-in adapter
 * translates the raw transport envelope into this typed command so the application core never touches
 * wire formats or payload maps.
 *
 * @param eventId      idempotency key (the outbox row id carried in the envelope)
 * @param orderId      aggregate id the event is about
 * @param customerName denormalised for logging/side effects
 */
public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        String customerName
) {
}
