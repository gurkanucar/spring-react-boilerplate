package com.gucardev.springreactboilerplate.features.order.entity;

/**
 * Lifecycle of an {@link Order} in the demo.
 *
 * <pre>
 *   PLACED ──(consumer handles the OrderCreated event)──▶ CONFIRMED
 * </pre>
 *
 * <p>The flip to {@code CONFIRMED} happening only after the consumer processes the event is what
 * lets the demo prove the message travelled end-to-end through the outbox → relay → broker → consumer.
 */
public enum OrderStatus {
    PLACED,
    CONFIRMED
}
