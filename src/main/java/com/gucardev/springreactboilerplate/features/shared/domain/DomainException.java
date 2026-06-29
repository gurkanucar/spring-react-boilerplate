package com.gucardev.springreactboilerplate.features.shared.domain;

/**
 * Base type for exceptions raised from inside the domain (aggregates / value objects) when a business
 * rule or invariant is violated.
 *
 * <p>It is deliberately <b>pure</b>: it knows nothing about HTTP, Spring or i18n. The domain expresses
 * <i>what</i> rule was broken and <i>which category</i> it falls into; the web layer
 * ({@code GlobalExceptionHandler}) is the adapter that translates the category into an HTTP status.
 * This is what lets aggregates enforce their own invariants without depending on the framework.
 *
 * @see com.gucardev.springreactboilerplate.features.order.domain.model.Order for a worked example
 */
public abstract class DomainException extends RuntimeException {

    /** Coarse classification the HTTP boundary maps to a status code. */
    public enum Category {
        /** Invariant / input rule broken — maps to 422 Unprocessable Entity. */
        VALIDATION,
        /** Illegal state transition / conflicting state — maps to 409 Conflict. */
        CONFLICT,
        /** Referenced aggregate does not exist — maps to 404 Not Found. */
        NOT_FOUND
    }

    private final Category category;
    private final String code;

    protected DomainException(Category category, String code, String message) {
        super(message);
        this.category = category;
        this.code = code;
    }

    public Category getCategory() {
        return category;
    }

    /** Stable, machine-readable error code (e.g. {@code ORDER_NOT_CONFIRMABLE}). */
    public String getCode() {
        return code;
    }
}
