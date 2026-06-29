package com.gucardev.springreactboilerplate.features.order.application.port.out;

import com.gucardev.springreactboilerplate.features.order.domain.model.Order;

/**
 * Output port: record an {@code OrderCreated} event for later publication. The application core states
 * the intent; the driven adapter owns the mechanism (transactional outbox row, routing key, payload
 * shape) so none of that infrastructure leaks into the core.
 *
 * <p>Implementations must enlist in the caller's transaction so the event and the business write
 * commit atomically — that is the whole point of the outbox pattern.
 */
public interface RecordOrderCreatedEventPort {

    void record(Order order);
}
