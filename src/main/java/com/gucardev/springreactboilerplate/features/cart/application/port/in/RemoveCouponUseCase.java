package com.gucardev.springreactboilerplate.features.cart.application.port.in;

import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;
import java.util.UUID;

/**
 * Input port: remove any applied coupon from a cart.
 */
public interface RemoveCouponUseCase {

    Cart removeCoupon(UUID cartId);
}
