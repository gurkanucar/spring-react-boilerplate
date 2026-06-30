package com.gucardev.springreactboilerplate.features.cart.domain.model;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The cart's tunable business rules, gathered in one place and given names. Keeping these as named
 * constants (rather than magic numbers sprinkled through {@link Cart}) means the policy reads like a
 * spec: "at most 50 distinct lines, 99 of any one item, free shipping from 150, otherwise a 15 flat
 * fee". Change a rule here and every calculation that depends on it follows.
 *
 * <p>In a real system these would likely come from configuration or a pricing service; hard-coding
 * them keeps the demo self-contained while still showing where such rules belong — in the domain,
 * referenced by the aggregate, not scattered across services or controllers.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CartPolicy {

    /** A cart may hold at most this many distinct SKUs. */
    public static final int MAX_DISTINCT_LINES = 50;

    /** Any single line may carry at most this many units. */
    public static final int MAX_QUANTITY_PER_LINE = 99;

    /** Orders whose post-discount subtotal reaches this amount ship free. */
    public static final Money FREE_SHIPPING_THRESHOLD = Money.of(new BigDecimal("150.00"));

    /** Flat shipping fee charged when the free-shipping threshold is not met. */
    public static final Money FLAT_SHIPPING_FEE = Money.of(new BigDecimal("15.00"));
}
