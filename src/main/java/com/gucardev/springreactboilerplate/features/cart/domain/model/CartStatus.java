package com.gucardev.springreactboilerplate.features.cart.domain.model;

/**
 * Lifecycle of a {@link Cart}.
 *
 * <pre>
 *   ACTIVE ──(checkout)──▶ CHECKED_OUT
 * </pre>
 *
 * <p>Only an {@code ACTIVE} cart accepts mutations (add/remove/change line, apply/remove coupon).
 * {@code checkout()} is the one-way door to {@code CHECKED_OUT}; after it, every mutation is rejected,
 * which is what makes a checked-out cart an immutable, auditable record of what was ordered.
 */
public enum CartStatus {
    ACTIVE,
    CHECKED_OUT
}
