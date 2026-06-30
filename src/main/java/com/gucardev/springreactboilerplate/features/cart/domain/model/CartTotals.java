package com.gucardev.springreactboilerplate.features.cart.domain.model;

import lombok.Getter;

/**
 * The priced-out result of a {@link Cart}: subtotal, discount, shipping and the resulting grand total.
 * A computed value object — never stored, always derived from the cart's current lines and coupon — so
 * it can't drift out of sync with the cart. {@link #of} owns the one assembly rule
 * ({@code total = subtotal − discount + shipping}), with {@link Money#minus} clamping at zero so the
 * total can never go negative.
 */
@Getter
public final class CartTotals {

    private final Money subtotal;
    private final Money discount;
    private final Money shipping;
    private final Money total;

    private CartTotals(Money subtotal, Money discount, Money shipping, Money total) {
        this.subtotal = subtotal;
        this.discount = discount;
        this.shipping = shipping;
        this.total = total;
    }

    public static CartTotals of(Money subtotal, Money discount, Money shipping) {
        Money total = subtotal.minus(discount).plus(shipping);
        return new CartTotals(subtotal, discount, shipping, total);
    }
}
