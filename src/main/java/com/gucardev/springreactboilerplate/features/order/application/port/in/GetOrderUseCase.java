package com.gucardev.springreactboilerplate.features.order.application.port.in;

import com.gucardev.springreactboilerplate.features.order.domain.model.Order;
import java.util.UUID;

/**
 * Input port: read a single order — handy for watching its status flip from PLACED to CONFIRMED after
 * the consumer handles the event.
 */
public interface GetOrderUseCase {

    Order getById(UUID id);
}
