package com.gucardev.springreactboilerplate.features.cart.domain.model;

import com.gucardev.springreactboilerplate.features.cart.domain.exception.InvalidCartException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object for a stock-keeping unit — the line identity inside a cart. Normalised to upper-case and
 * constrained to a stable format ({@code A-Z}, {@code 0-9} and dashes, 2–32 chars), so two adds of
 * {@code "kbd-01"} and {@code "KBD-01"} are recognised as the <i>same</i> product and merged rather
 * than creating a duplicate line. Equality is by the normalised value.
 */
public final class Sku {

    private static final Pattern FORMAT = Pattern.compile("^[A-Z0-9][A-Z0-9-]{1,31}$");

    private final String value;

    private Sku(String value) {
        this.value = value;
    }

    public static Sku of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidCartException("CART_SKU_REQUIRED", "SKU is required.");
        }
        String normalised = raw.strip().toUpperCase();
        if (!FORMAT.matcher(normalised).matches()) {
            throw new InvalidCartException("CART_SKU_INVALID",
                    "SKU '" + raw + "' is invalid — use 2–32 chars of A–Z, 0–9 and dashes.");
        }
        return new Sku(normalised);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Sku other) && Objects.equals(value, other.value);
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
