package com.gucardev.springreactboilerplate.features.order.domain.exception;

import com.gucardev.springreactboilerplate.features.shared.domain.DomainException;

/**
 * Raised when an order (or one of its value objects) would violate a creation invariant — e.g. a
 * non-positive quantity, a negative amount, or a blank required field. Maps to HTTP 422.
 */
public class InvalidOrderException extends DomainException {

    public InvalidOrderException(String code, String message) {
        super(Category.VALIDATION, code, message);
    }
}
