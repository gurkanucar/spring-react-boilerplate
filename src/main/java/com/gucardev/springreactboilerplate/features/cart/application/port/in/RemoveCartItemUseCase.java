package com.gucardev.springreactboilerplate.features.cart.application.port.in;

import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;
import java.util.UUID;

/**
 * Input port: remove a line from a cart by SKU.
 */
public interface RemoveCartItemUseCase {

    Cart removeItem(UUID cartId, String sku);
}
