package com.gucardev.springreactboilerplate.features.cart.application.port.in;

import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;
import java.util.UUID;

/**
 * Input port: check a cart out, locking it for ordering.
 */
public interface CheckoutCartUseCase {

    Cart checkout(UUID cartId);
}
