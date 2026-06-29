package com.gucardev.springreactboilerplate.features.order.domain.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Shared constants for the {@code OrderCreated} event so the producer (which writes the outbox row)
 * and the consumer (which reads the envelope) agree on the event type and payload keys — no
 * magic-string drift between the two sides.
 *
 * <p>This is part of the domain: it is the event contract the application core defines. The driving
 * (messaging-in) and driven (messaging-out) adapters both reference it when they translate between
 * the wire envelope and the application's typed model.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderEvents {

    public static final String AGGREGATE_TYPE = "Order";
    public static final String ORDER_CREATED = "OrderCreated";

    /** The consumer that handles {@code OrderCreated} — recorded in the inbox for idempotency. */
    public static final String ORDER_CREATED_CONSUMER = "order-created-consumer";

    // Payload keys carried in the event body.
    public static final String KEY_ORDER_ID = "orderId";
    public static final String KEY_CUSTOMER_NAME = "customerName";
    public static final String KEY_PRODUCT = "product";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_AMOUNT = "amount";
}
