package com.gucardev.springreactboilerplate.features.cart.adapter.in.web;

import com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto.CartLineResponse;
import com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto.CartResponse;
import com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto.CartTotalsResponse;
import com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto.CouponResponse;
import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;
import com.gucardev.springreactboilerplate.features.cart.domain.model.CartLine;
import com.gucardev.springreactboilerplate.features.cart.domain.model.CartTotals;
import com.gucardev.springreactboilerplate.features.cart.domain.model.Coupon;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Maps the {@link Cart} domain model to its web response. Hand-written (not MapStruct) because the
 * response is nested and, crucially, <b>derived</b>: the totals block is produced by calling
 * {@code cart.totals()} — the pricing rule stays in the domain, the mapper just shapes the result for
 * the wire and unwraps the value objects.
 */
@Component
public class CartWebMapper {

    public CartResponse toResponse(Cart cart) {
        CartTotals totals = cart.totals();
        return CartResponse.builder()
                .id(cart.getId())
                .customerName(cart.getCustomerName())
                .status(cart.getStatus())
                .lines(cart.getLines().stream().map(this::toLineResponse).toList())
                .coupon(cart.getCoupon() == null ? null : toCouponResponse(cart.getCoupon()))
                .totals(toTotalsResponse(totals))
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .createdBy(cart.getCreatedBy())
                .updatedBy(cart.getUpdatedBy())
                .build();
    }

    private CartLineResponse toLineResponse(CartLine line) {
        return CartLineResponse.builder()
                .sku(line.getSku().value())
                .productName(line.getProductName().value())
                .unitPrice(line.getUnitPrice().value())
                .quantity(line.getQuantity().value())
                .lineTotal(line.lineTotal().value())
                .build();
    }

    private CouponResponse toCouponResponse(Coupon coupon) {
        return CouponResponse.builder()
                .code(coupon.getCode())
                .type(coupon.getType())
                .value(coupon.getValue())
                .minSubtotal(coupon.getMinSubtotal().value())
                .maxDiscount(coupon.getMaxDiscount() == null ? null : coupon.getMaxDiscount().value())
                .build();
    }

    private CartTotalsResponse toTotalsResponse(CartTotals totals) {
        return CartTotalsResponse.builder()
                .subtotal(totals.getSubtotal().value())
                .discount(totals.getDiscount().value())
                .shipping(totals.getShipping().value())
                .total(totals.getTotal().value())
                .build();
    }
}
