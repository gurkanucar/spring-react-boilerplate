package com.gucardev.springreactboilerplate.features.order.domain.model;

import com.gucardev.springreactboilerplate.features.order.domain.exception.InvalidOrderException;
import java.util.Objects;

/**
 * Value object for an ordered quantity. Self-validating: a {@code Quantity} can only exist if it is at
 * least 1, so the invariant is guaranteed everywhere one is held — no caller can construct an invalid
 * one. Immutable; equality is by value.
 */
public final class Quantity {

    private final Integer value;

    private Quantity(Integer value) {
        this.value = value;
    }

    public static Quantity of(Integer value) {
        if (value == null) {
            throw new InvalidOrderException("ORDER_QUANTITY_REQUIRED", "Order quantity is required.");
        }
        if (value < 1) {
            throw new InvalidOrderException("ORDER_QUANTITY_INVALID",
                    "Order quantity must be at least 1 but was " + value + ".");
        }
        return new Quantity(value);
    }

    public Integer value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Quantity other) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
