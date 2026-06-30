package com.gucardev.springreactboilerplate.features.cart.application.port.in;

import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;
import java.util.UUID;

/**
 * Input port: read a single cart, priced out via its {@code totals()}.
 */
public interface GetCartUseCase {

    Cart getById(UUID id);
}
