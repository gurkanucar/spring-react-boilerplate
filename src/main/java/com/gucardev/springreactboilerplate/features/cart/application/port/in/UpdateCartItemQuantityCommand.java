package com.gucardev.springreactboilerplate.features.cart.application.port.in;

import java.util.UUID;

/** Driving-side command for setting a line's quantity to an exact value (0 removes the line). */
public record UpdateCartItemQuantityCommand(
        UUID cartId,
        String sku,
        Integer quantity
) {
}
