package com.gucardev.springreactboilerplate.features.cart.domain.model;

import com.gucardev.springreactboilerplate.features.cart.domain.exception.InvalidCartException;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.Getter;

/**
 * Value object for a discount coupon. It owns the whole "what is this coupon worth on a given
 * subtotal?" rule — {@link Cart} just asks it. This is a small policy/strategy living inside a value
 * object: the aggregate never branches on coupon type, it delegates.
 *
 * <p>Construction enforces the coupon makes sense for its {@link CouponType}:
 * <ul>
 *   <li><b>PERCENTAGE</b>: {@code value} is a percentage in (0, 100]; an optional {@code maxDiscount}
 *       caps how much it can ever take off (a 20% coupon on a 10 000 cart shouldn't give away 2 000).</li>
 *   <li><b>FIXED</b>: {@code value} is a flat money amount &gt; 0; {@code maxDiscount} is not used.</li>
 * </ul>
 * and a {@code minSubtotal} gate (≥ 0) below which the coupon does not apply at all.
 */
@Getter
public final class Coupon {

    private final String code;
    private final CouponType type;
    private final BigDecimal value;
    private final Money minSubtotal;
    private final Money maxDiscount; // nullable; only meaningful for PERCENTAGE

    private Coupon(String code, CouponType type, BigDecimal value, Money minSubtotal, Money maxDiscount) {
        this.code = code;
        this.type = type;
        this.value = value;
        this.minSubtotal = minSubtotal;
        this.maxDiscount = maxDiscount;
    }

    /**
     * Validates and builds a coupon. {@code minSubtotal} defaults to zero when null; {@code maxDiscount}
     * is optional (percentage only).
     */
    public static Coupon of(String code, CouponType type, BigDecimal value,
                            BigDecimal minSubtotal, BigDecimal maxDiscount) {
        if (code == null || code.isBlank()) {
            throw new InvalidCartException("CART_COUPON_CODE_REQUIRED", "Coupon code is required.");
        }
        if (type == null) {
            throw new InvalidCartException("CART_COUPON_TYPE_REQUIRED", "Coupon type is required.");
        }
        if (value == null) {
            throw new InvalidCartException("CART_COUPON_VALUE_REQUIRED", "Coupon value is required.");
        }
        Money min = minSubtotal == null ? Money.zero() : Money.of(minSubtotal);
        Money cap = maxDiscount == null ? null : Money.of(maxDiscount);

        if (type == CouponType.PERCENTAGE) {
            if (value.signum() <= 0 || value.compareTo(new BigDecimal("100")) > 0) {
                throw new InvalidCartException("CART_COUPON_PERCENT_RANGE",
                        "Percentage coupon must be in (0, 100] but was " + value.toPlainString() + ".");
            }
        } else { // FIXED
            if (value.signum() <= 0) {
                throw new InvalidCartException("CART_COUPON_FIXED_NON_POSITIVE",
                        "Fixed coupon amount must be greater than 0 but was " + value.toPlainString() + ".");
            }
            cap = null; // a max-discount cap is meaningless for a fixed amount
        }
        return new Coupon(code.strip().toUpperCase(), type, value, min, cap);
    }

    /** Whether this coupon's minimum-spend gate is met by {@code subtotal}. */
    public boolean isEligibleFor(Money subtotal) {
        return subtotal.isGreaterThanOrEqualTo(minSubtotal);
    }

    /**
     * The discount this coupon yields on {@code subtotal} — the heart of the coupon rule.
     *
     * <ul>
     *   <li>not eligible (subtotal below the minimum) → no discount;</li>
     *   <li>PERCENTAGE → {@code value}% of the subtotal, then capped at {@code maxDiscount} if set;</li>
     *   <li>FIXED → the flat amount, but never more than the subtotal (a coupon can't pay the customer).</li>
     * </ul>
     */
    public Money discountFor(Money subtotal) {
        if (!isEligibleFor(subtotal)) {
            return Money.zero();
        }
        if (type == CouponType.PERCENTAGE) {
            Money raw = subtotal.percentage(value);
            return maxDiscount == null ? raw : raw.min(maxDiscount);
        }
        return Money.of(value).min(subtotal);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Coupon other)
                && Objects.equals(code, other.code)
                && type == other.type
                && value.compareTo(other.value) == 0
                && Objects.equals(minSubtotal, other.minSubtotal)
                && Objects.equals(maxDiscount, other.maxDiscount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, type, value.stripTrailingZeros(), minSubtotal, maxDiscount);
    }
}
