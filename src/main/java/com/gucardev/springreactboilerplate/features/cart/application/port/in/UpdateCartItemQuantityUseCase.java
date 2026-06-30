package com.gucardev.springreactboilerplate.features.cart.application.port.in;

import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;

/**
 * Input port: set the exact quantity of a line (zero removes it). The aggregate owns the rules.
 */
public interface UpdateCartItemQuantityUseCase {

    Cart updateQuantity(UpdateCartItemQuantityCommand command);
}
