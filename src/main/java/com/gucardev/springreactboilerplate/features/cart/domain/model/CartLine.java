package com.gucardev.springreactboilerplate.features.cart.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.Getter;

/**
 * A single line within a {@link Cart} — a child entity of the cart aggregate, identified by its
 * {@link Sku}. Built only from already-validated value objects, so a {@code CartLine} is correct by
 * construction. Immutable: quantity changes return a <i>new</i> line, so the cart can swap the line in
 * its list without anyone holding a stale, mutated reference.
 *
 * <p>It owns one piece of arithmetic — {@link #lineTotal()} = unit price × quantity — which is the
 * building block the cart sums into a subtotal.
 */
@Getter
public final class CartLine {

    private final Sku sku;
    private final ProductName productName;
    private final Money unitPrice;
    private final Quantity quantity;

    private CartLine(Sku sku, ProductName productName, Money unitPrice, Quantity quantity) {
        this.sku = sku;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /** Builds a line from raw inputs, delegating each field's validation to its value object. */
    public static CartLine of(String sku, String productName, BigDecimal unitPrice, Integer quantity) {
        return new CartLine(Sku.of(sku), ProductName.of(productName), Money.of(unitPrice), Quantity.of(quantity));
    }

    /** Rebuilds a line from stored state. Same validation as {@link #of}, no aggregate-level limits. */
    public static CartLine fromPersistence(String sku, String productName, BigDecimal unitPrice, Integer quantity) {
        return of(sku, productName, unitPrice, quantity);
    }

    /** Returns a copy of this line with its quantity replaced. */
    public CartLine withQuantity(Quantity newQuantity) {
        return new CartLine(sku, productName, unitPrice, newQuantity);
    }

    /** Returns a copy of this line with {@code extra} units added to the current quantity. */
    public CartLine increasedBy(Quantity extra) {
        return new CartLine(sku, productName, unitPrice, quantity.plus(extra));
    }

    /** This line's contribution to the subtotal: unit price × quantity. */
    public Money lineTotal() {
        return unitPrice.times(quantity.value());
    }

    public boolean hasSku(Sku other) {
        return sku.equals(other);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof CartLine other)
                && Objects.equals(sku, other.sku)
                && Objects.equals(productName, other.productName)
                && Objects.equals(unitPrice, other.unitPrice)
                && Objects.equals(quantity, other.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku, productName, unitPrice, quantity);
    }
}
