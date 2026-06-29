package com.gucardev.springreactboilerplate.features.order.application.port.in;

/**
 * Input port: consumer-side handler for {@code OrderCreated}. Invoked by the messaging-in adapter; the
 * implementation is idempotent because delivery is at-least-once.
 */
public interface HandleOrderCreatedUseCase {

    void handle(OrderCreatedEvent event);
}
