package com.gucardev.springreactboilerplate.features.cart.application.port.in;

import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;

/**
 * Input port: add a product (or more of one already present) to a cart. The merge / limit rules live
 * in the {@code Cart} aggregate; this port is just the entry point.
 */
public interface AddCartItemUseCase {

    Cart addItem(AddCartItemCommand command);
}
