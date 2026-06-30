package com.gucardev.springreactboilerplate.features.cart.domain.exception;

import com.gucardev.springreactboilerplate.features.shared.domain.DomainException;

/**
 * Raised when an operation targets a line (by SKU) that is not in the cart — e.g. changing the
 * quantity of, or removing, a SKU the cart does not contain. Maps to HTTP 404.
 */
public class CartLineNotFoundException extends DomainException {

    public CartLineNotFoundException(String sku) {
        super(Category.NOT_FOUND, "CART_LINE_NOT_FOUND",
                "Cart has no line for SKU '" + sku + "'.");
    }
}
