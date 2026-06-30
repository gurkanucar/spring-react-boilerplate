package com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto;

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
@Schema(description = "The cart priced out: subtotal, discount, shipping and grand total. Always derived, never stored.")
public class CartTotalsResponse {

    @Schema(description = "Sum of all line totals", example = "259.80")
    private BigDecimal subtotal;

    @Schema(description = "Discount from the applied coupon (0 if none / not eligible)", example = "25.98")
    private BigDecimal discount;

    @Schema(description = "Shipping fee (0 once the post-discount subtotal reaches the free-shipping threshold)",
            example = "15.00")
    private BigDecimal shipping;

    @Schema(description = "subtotal − discount + shipping", example = "248.82")
    private BigDecimal total;
}
