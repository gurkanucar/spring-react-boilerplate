package com.gucardev.springreactboilerplate.features.cart.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Driving-side command for adding an item to a cart. The raw fields are handed to the aggregate, which
 * turns them into validated value objects.
 */
public record AddCartItemCommand(
        UUID cartId,
        String sku,
        String productName,
        BigDecimal unitPrice,
        Integer quantity
) {
}
