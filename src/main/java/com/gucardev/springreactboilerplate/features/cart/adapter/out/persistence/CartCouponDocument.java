package com.gucardev.springreactboilerplate.features.cart.adapter.out.persistence;

import java.math.BigDecimal;

/**
 * Persistence-side, flat representation of a {@code Coupon}, stored in the cart row's {@code coupon}
 * JSON column (null when no coupon is applied). {@code type} is the {@code CouponType} name; the mapper
 * rebuilds a validated {@code Coupon} from it on load.
 */
public record CartCouponDocument(
        String code,
        String type,
        BigDecimal value,
        BigDecimal minSubtotal,
        BigDecimal maxDiscount
) {
}
