package com.gucardev.springreactboilerplate.features.cart.application.port.in;

import com.gucardev.springreactboilerplate.features.cart.domain.model.CouponType;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Driving-side command for applying a coupon. {@code value} is a percentage (1–100) when {@code type}
 * is PERCENTAGE, or a flat amount when FIXED. {@code minSubtotal} and {@code maxDiscount} are optional.
 */
public record ApplyCouponCommand(
        UUID cartId,
        String code,
        CouponType type,
        BigDecimal value,
        BigDecimal minSubtotal,
        BigDecimal maxDiscount
) {
}
