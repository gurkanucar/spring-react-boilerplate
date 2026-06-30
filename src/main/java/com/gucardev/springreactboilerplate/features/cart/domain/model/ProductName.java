package com.gucardev.springreactboilerplate.features.cart.domain.model;

import com.gucardev.springreactboilerplate.features.cart.domain.exception.InvalidCartException;
import java.util.Objects;

/**
 * Value object for a product's display name on a cart line. Self-validating: non-blank and at most 150
 * characters, trimmed. Keeps the "is the name sane?" rule out of the aggregate and the DTOs.
 */
public final class ProductName {

    private static final int MAX_LENGTH = 150;

    private final String value;

    private ProductName(String value) {
        this.value = value;
    }

    public static ProductName of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidCartException("CART_PRODUCT_NAME_REQUIRED", "Product name is required.");
        }
        String trimmed = raw.strip();
        if (trimmed.length() > MAX_LENGTH) {
            throw new InvalidCartException("CART_PRODUCT_NAME_TOO_LONG",
                    "Product name must be at most " + MAX_LENGTH + " characters.");
        }
        return new ProductName(trimmed);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ProductName other) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
