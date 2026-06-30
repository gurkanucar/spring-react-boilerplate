package com.gucardev.springreactboilerplate.features.cart.domain.model;

import com.gucardev.springreactboilerplate.features.cart.domain.exception.InvalidCartException;
import java.util.Objects;

/**
 * Value object for an ordered quantity. A {@code Quantity} can only exist if it is at least 1, so the
 * invariant is guaranteed everywhere one is held. Immutable; {@link #plus} returns a new value, which
 * is how a cart merges a repeat add of the same SKU into the existing line.
 */
public final class Quantity {

    private final Integer value;

    private Quantity(Integer value) {
        this.value = value;
    }

    public static Quantity of(Integer value) {
        if (value == null) {
            throw new InvalidCartException("CART_QUANTITY_REQUIRED", "Quantity is required.");
        }
        if (value < 1) {
            throw new InvalidCartException("CART_QUANTITY_INVALID",
                    "Quantity must be at least 1 but was " + value + ".");
        }
        return new Quantity(value);
    }

    public Quantity plus(Quantity other) {
        return new Quantity(this.value + other.value);
    }

    public boolean isGreaterThan(int max) {
        return value > max;
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
