package com.gucardev.springreactboilerplate.features.cart.adapter.out.persistence;

import java.math.BigDecimal;

/**
 * Persistence-side, flat representation of a {@code CartLine}, stored inside the cart row's {@code lines}
 * JSON array. Deliberately a dumb record of primitives/strings — it carries no domain rules; the
 * {@code CartPersistenceMapper} turns it back into a validated {@code CartLine} value object on load.
 */
public record CartLineDocument(
        String sku,
        String productName,
        BigDecimal unitPrice,
        Integer quantity
) {
}
