package com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto;

import com.gucardev.springreactboilerplate.features.cart.domain.model.CouponType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "The coupon applied to a cart, if any.")
public class CouponResponse {

    @Schema(description = "Coupon code (normalised to upper-case)", example = "WELCOME10")
    private String code;

    @Schema(description = "PERCENTAGE or FIXED", example = "PERCENTAGE")
    private CouponType type;

    @Schema(description = "Percentage (1–100) for PERCENTAGE, or flat amount for FIXED", example = "10")
    private BigDecimal value;

    @Schema(description = "Minimum subtotal required for the coupon to apply", example = "100.00")
    private BigDecimal minSubtotal;

    @Schema(description = "Maximum discount cap (PERCENTAGE only; null when uncapped/FIXED)", example = "50.00")
    private BigDecimal maxDiscount;
}
