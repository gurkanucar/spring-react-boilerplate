package com.gucardev.springreactboilerplate.features.order.domain.model;

import com.gucardev.springreactboilerplate.features.order.domain.exception.InvalidOrderException;
import com.gucardev.springreactboilerplate.features.order.domain.exception.OrderNotConfirmableException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

/**
 * The order aggregate — the rich-domain showcase at the centre of the hexagon.
 *
 * <p>It is a proper aggregate, not an anemic data holder:
 * <ul>
 *   <li><b>Encapsulated</b> — no public constructor, no setters, no builder. State changes only ever
 *       happen through behavior methods, so the object cannot be put into an invalid state from outside.</li>
 *   <li><b>Created through a factory</b> — {@link #place} enforces every creation invariant (required
 *       fields, valid {@link Quantity} and {@link Money}) and is the ONLY way to mint a brand-new order.
 *       New orders always start {@link OrderStatus#PLACED}.</li>
 *   <li><b>Rehydrated through a separate factory</b> — {@link #fromPersistence} rebuilds an order from
 *       stored state (used by the persistence adapter) without re-running creation rules.</li>
 *   <li><b>Guards its lifecycle</b> — {@link #confirm()} is a state-machine transition that rejects
 *       illegal moves ({@code PLACED → CONFIRMED} only), which also makes duplicate-event handling safe.</li>
 *   <li><b>Uses value objects</b> — {@link Quantity} and {@link Money} carry their own invariants, so the
 *       aggregate never has to re-check "is the amount negative?" — an invalid amount can't be constructed.</li>
 * </ul>
 *
 * <p>The model carries no JPA/Spring/serialization annotations; adapters map to/from it.
 */
@Getter
public final class Order {

    private final UUID id;
    private final String customerName;
    private final String product;
    private final Quantity quantity;
    private final Money amount;
    private OrderStatus status;

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;
    private final String updatedBy;

    private Order(UUID id, String customerName, String product, Quantity quantity, Money amount,
                  OrderStatus status, LocalDateTime createdAt, LocalDateTime updatedAt,
                  String createdBy, String updatedBy) {
        this.id = id;
        this.customerName = customerName;
        this.product = product;
        this.quantity = quantity;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    /**
     * Factory for a brand-new order. Enforces all creation invariants; the resulting order is
     * {@link OrderStatus#PLACED} and has no id yet (the persistence adapter assigns one on save).
     *
     * @throws InvalidOrderException if any field violates a business rule
     */
    public static Order place(String customerName, String product, Integer quantity, BigDecimal amount) {
        return new Order(
                null,
                requireText(customerName, "ORDER_CUSTOMER_NAME_REQUIRED", "Customer name"),
                requireText(product, "ORDER_PRODUCT_REQUIRED", "Product"),
                Quantity.of(quantity),
                Money.of(amount),
                OrderStatus.PLACED,
                null, null, null, null);
    }

    /**
     * Rebuilds an order from already-persisted state. Used by the persistence adapter only — it trusts
     * the stored status and audit metadata rather than re-running the creation lifecycle.
     */
    public static Order fromPersistence(UUID id, String customerName, String product, Integer quantity,
                                        BigDecimal amount, OrderStatus status, LocalDateTime createdAt,
                                        LocalDateTime updatedAt, String createdBy, String updatedBy) {
        return new Order(id, customerName, product, Quantity.of(quantity), Money.of(amount), status,
                createdAt, updatedAt, createdBy, updatedBy);
    }

    /**
     * Lifecycle transition: confirm the order once its creation event has been consumed end-to-end.
     * Only a {@link OrderStatus#PLACED} order may be confirmed; attempting to confirm from any other
     * state is an illegal transition (which is exactly what makes redelivered events harmless — a
     * second confirm of an already-CONFIRMED order is rejected rather than silently re-applied).
     *
     * @throws OrderNotConfirmableException if the order is not currently {@code PLACED}
     */
    public void confirm() {
        if (status != OrderStatus.PLACED) {
            throw new OrderNotConfirmableException(
                    "Order " + id + " cannot be confirmed from status " + status
                            + " — only PLACED orders can be confirmed.");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public boolean isConfirmed() {
        return status == OrderStatus.CONFIRMED;
    }

    private static String requireText(String value, String code, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidOrderException(code, field + " is required.");
        }
        return value.strip();
    }
}
