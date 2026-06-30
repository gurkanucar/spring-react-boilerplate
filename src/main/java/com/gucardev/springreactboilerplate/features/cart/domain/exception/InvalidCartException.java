package com.gucardev.springreactboilerplate.features.cart.domain.exception;

import com.gucardev.springreactboilerplate.features.shared.domain.DomainException;

/**
 * Raised when a cart (or one of its value objects) would violate a creation/input invariant — e.g. a
 * blank product name, a malformed SKU, a non-positive quantity or a negative price. Maps to HTTP 422.
 */
public class InvalidCartException extends DomainException {

    public InvalidCartException(String code, String message) {
        super(Category.VALIDATION, code, message);
    }
}
