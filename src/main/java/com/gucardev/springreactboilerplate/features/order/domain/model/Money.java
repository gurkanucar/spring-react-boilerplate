package com.gucardev.springreactboilerplate.features.order.domain.model;

import com.gucardev.springreactboilerplate.features.order.domain.exception.InvalidOrderException;
import java.math.BigDecimal;

/**
 * Value object for a monetary amount. Self-validating: non-null, non-negative, at most 2 decimal
 * places — so any {@code Money} in the system is by construction a sane amount. Immutable; equality is
 * by numeric value (so {@code 1.5} equals {@code 1.50}).
 */
public final class Money {

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        this.amount = amount;
    }

    public static Money of(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidOrderException("ORDER_AMOUNT_REQUIRED", "Order amount is required.");
        }
        if (amount.signum() < 0) {
            throw new InvalidOrderException("ORDER_AMOUNT_NEGATIVE",
                    "Order amount must not be negative but was " + amount.toPlainString() + ".");
        }
        if (amount.scale() > 2) {
            throw new InvalidOrderException("ORDER_AMOUNT_SCALE",
                    "Order amount must have at most 2 decimal places but was " + amount.toPlainString() + ".");
        }
        return new Money(amount);
    }

    public BigDecimal value() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Money other) && amount.compareTo(other.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
