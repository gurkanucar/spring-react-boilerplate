package com.gucardev.springreactboilerplate.features.order.application.port.in;

import com.gucardev.springreactboilerplate.features.order.domain.model.Order;

/**
 * Input port: place an order (the producer side of the outbox demo). Driving adapters depend on this
 * interface, not on the implementing service.
 */
public interface PlaceOrderUseCase {

    Order place(PlaceOrderCommand command);
}
