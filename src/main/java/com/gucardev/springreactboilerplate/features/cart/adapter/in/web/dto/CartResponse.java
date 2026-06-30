package com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto;

import com.gucardev.springreactboilerplate.features.cart.domain.model.CartStatus;
import com.gucardev.springreactboilerplate.features.shared.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A shopping cart: its lines, the applied coupon, the derived totals and audit metadata.")
public class CartResponse extends BaseDto {

    @Schema(description = "Cart id")
    private UUID id;

    @Schema(description = "Customer the cart belongs to", example = "Ada Lovelace")
    private String customerName;

    @Schema(description = "Lifecycle status; ACTIVE accepts changes, CHECKED_OUT is frozen", example = "ACTIVE")
    private CartStatus status;

    @Schema(description = "The cart's lines")
    private List<CartLineResponse> lines;

    @Schema(description = "The applied coupon, or null when none is applied")
    private CouponResponse coupon;

    @Schema(description = "The cart priced out (subtotal, discount, shipping, total)")
    private CartTotalsResponse totals;
}
