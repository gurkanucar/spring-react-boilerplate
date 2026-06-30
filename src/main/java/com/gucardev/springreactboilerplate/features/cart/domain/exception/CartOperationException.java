package com.gucardev.springreactboilerplate.features.cart.domain.exception;

import com.gucardev.springreactboilerplate.features.shared.domain.DomainException;

/**
 * Raised when an operation is illegal for the cart's current state, rather than because an input is
 * malformed. Examples: modifying a cart that has already been checked out, exceeding the per-line or
 * per-cart limits, applying a coupon to an empty cart, applying a second coupon, applying a coupon
 * whose minimum-spend threshold is not met, or checking out an empty cart.
 *
 * <p>These are conflicts with the aggregate's current state / business rules, so they map to HTTP 409.
 */
public class CartOperationException extends DomainException {

    public CartOperationException(String code, String message) {
        super(Category.CONFLICT, code, message);
    }
}
