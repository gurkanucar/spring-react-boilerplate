package com.gucardev.springreactboilerplate.features.cart.application.port.in;

import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;

/**
 * Input port: apply a discount coupon to a cart. Eligibility, the single-coupon rule and the discount
 * maths all live in the {@code Cart}/{@code Coupon} domain types.
 */
public interface ApplyCouponUseCase {

    Cart applyCoupon(ApplyCouponCommand command);
}
