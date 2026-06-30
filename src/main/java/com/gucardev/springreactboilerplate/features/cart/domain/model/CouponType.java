package com.gucardev.springreactboilerplate.features.cart.domain.model;

/**
 * The two discount shapes a {@link Coupon} can take.
 *
 * <ul>
 *   <li>{@link #PERCENTAGE} — a percentage off the subtotal (e.g. 10%), optionally capped at a maximum
 *       discount amount so a percentage coupon can't give away an unbounded sum on a large cart.</li>
 *   <li>{@link #FIXED} — a flat amount off (e.g. 25 off), never more than the subtotal itself.</li>
 * </ul>
 */
public enum CouponType {
    PERCENTAGE,
    FIXED
}
