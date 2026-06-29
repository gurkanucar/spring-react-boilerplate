package com.gucardev.springreactboilerplate.features.order.application.port.out;

import java.util.UUID;

/**
 * Output port: the consumer-side idempotency ledger (inbox). Lets the handler skip events it has
 * already processed and mark new ones as done — within the same transaction as the side effect.
 */
public interface ProcessedEventPort {

    boolean isProcessed(UUID eventId);

    void markProcessed(UUID eventId, String consumer);
}
