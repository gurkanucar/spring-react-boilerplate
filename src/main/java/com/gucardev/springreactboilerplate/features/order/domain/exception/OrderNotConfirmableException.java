package com.gucardev.springreactboilerplate.features.order.domain.exception;

import com.gucardev.springreactboilerplate.features.shared.domain.DomainException;

/**
 * Raised when {@code confirm()} is attempted on an order that is not in a confirmable state (i.e. not
 * {@code PLACED}). This guards the lifecycle state machine against illegal transitions (e.g. a
 * duplicate event trying to confirm an already-confirmed order). Maps to HTTP 409 Conflict.
 */
public class OrderNotConfirmableException extends DomainException {

    public OrderNotConfirmableException(String message) {
        super(Category.CONFLICT, "ORDER_NOT_CONFIRMABLE", message);
    }
}
