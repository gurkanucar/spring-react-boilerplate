package com.gucardev.springreactboilerplate.features.cart.domain.model;

import com.gucardev.springreactboilerplate.features.cart.domain.exception.InvalidCartException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value object for a monetary amount (single, implicit currency for the demo).
 *
 * <p>Self-validating and <b>self-calculating</b>: it is non-null and non-negative by construction, and
 * it carries the arithmetic the cart needs ({@link #plus}, {@link #minus}, {@link #times},
 * {@link #percentage}) so the aggregate never hand-rolls {@code BigDecimal} math or worries about
 * scale/rounding. Every amount is normalised to exactly two decimal places (banker-free
 * {@link RoundingMode#HALF_UP}), so equality is intuitive and totals never drift by a rogue tenth of a
 * cent. Immutable; every operation returns a new {@code Money}.
 *
 * <p>This is the workhorse value object of the rich-domain showcase: pushing the money rules in here is
 * what keeps {@link Cart}'s pricing logic readable.
 */
public final class Money {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        this.amount = amount;
    }

    /** Builds a money value, rejecting null/negative amounts and normalising to 2 decimal places. */
    public static Money of(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidCartException("CART_AMOUNT_REQUIRED", "Amount is required.");
        }
        if (amount.signum() < 0) {
            throw new InvalidCartException("CART_AMOUNT_NEGATIVE",
                    "Amount must not be negative but was " + amount.toPlainString() + ".");
        }
        return new Money(amount.setScale(2, RoundingMode.HALF_UP));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    public Money plus(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    /**
     * Subtracts {@code other}, never going below zero — a discount or refund can at most wipe an
     * amount out, it cannot turn it negative. Clamping here is what lets the aggregate subtract a
     * discount from a subtotal without a separate "did we overshoot?" guard.
     */
    public Money minus(Money other) {
        BigDecimal result = this.amount.subtract(other.amount);
        return new Money(result.signum() < 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : result);
    }

    /** Multiplies by a (positive) count, e.g. unit price × quantity for a line total. */
    public Money times(int count) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(count)).setScale(2, RoundingMode.HALF_UP));
    }

    /** Returns {@code percent}% of this amount, e.g. {@code Money.of(120).percentage(10)} = {@code 12.00}. */
    public Money percentage(BigDecimal percent) {
        return new Money(this.amount.multiply(percent)
                .divide(HUNDRED, 2, RoundingMode.HALF_UP));
    }

    public Money min(Money other) {
        return this.amount.compareTo(other.amount) <= 0 ? this : other;
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isGreaterThanOrEqualTo(Money other) {
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isLessThan(Money other) {
        return this.amount.compareTo(other.amount) < 0;
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
