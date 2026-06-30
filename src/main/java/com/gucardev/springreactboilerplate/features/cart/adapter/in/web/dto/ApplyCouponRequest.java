package com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto;

import com.gucardev.springreactboilerplate.features.cart.domain.model.CouponType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Apply a coupon to a cart. {@code value} is a percentage (1–100) when {@code type} is PERCENTAGE, or a
 * flat amount when FIXED. The eligibility gate ({@code minSubtotal}) and the percentage cap
 * ({@code maxDiscount}) are optional; the discount maths and the single-coupon / minimum-spend rules are
 * enforced by the {@code Coupon} value object and the {@code Cart} aggregate.
 */
@Schema(description = "Apply a discount coupon to a cart.")
public record ApplyCouponRequest(

        @Schema(description = "Coupon code (case-insensitive)", example = "WELCOME10")
        @NotBlank
        String code,

        @Schema(description = "PERCENTAGE or FIXED", example = "PERCENTAGE")
        @NotNull
        CouponType type,

        @Schema(description = "Percentage (1–100) for PERCENTAGE, or flat amount for FIXED", example = "10")
        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal value,

        @Schema(description = "Optional minimum subtotal required for the coupon to apply", example = "100.00")
        @Digits(integer = 10, fraction = 2)
        BigDecimal minSubtotal,

        @Schema(description = "Optional maximum discount cap (PERCENTAGE only)", example = "50.00")
        @Digits(integer = 10, fraction = 2)
        BigDecimal maxDiscount
) {
}
