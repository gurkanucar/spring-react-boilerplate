package com.gucardev.springreactboilerplate.features.order.application.port.out;

import com.gucardev.springreactboilerplate.features.order.domain.model.Order;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port: load an order from the store. Implemented by a driven persistence adapter.
 */
public interface LoadOrderPort {

    Optional<Order> findById(UUID id);
}
