package com.gucardev.springreactboilerplate.features.cart.application.port.in;

import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;

/**
 * Input port: open a new, empty cart for a customer. Driving adapters depend on this interface, not on
 * the implementing service.
 */
public interface CreateCartUseCase {

    Cart create(CreateCartCommand command);
}
