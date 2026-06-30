package com.gucardev.springreactboilerplate.features.cart.domain.model;

import com.gucardev.springreactboilerplate.features.cart.domain.exception.CartLineNotFoundException;
import com.gucardev.springreactboilerplate.features.cart.domain.exception.CartOperationException;
import com.gucardev.springreactboilerplate.features.cart.domain.exception.InvalidCartException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;

/**
 * The shopping-cart aggregate — the rich-domain showcase. Unlike an anemic entity that is just a bag of
 * getters/setters with the "real" logic living in a service, <b>this aggregate owns its rules</b>. The
 * application services around it only load it, call one behaviour method, and save it; every decision
 * about what is and isn't a legal change is made here.
 *
 * <p><b>What the aggregate guarantees:</b>
 * <ul>
 *   <li><b>Encapsulation</b> — no public constructor, no setters. The internal line list is never
 *       handed out mutable ({@link #getLines()} returns an unmodifiable copy), so the only way to
 *       change a cart is through its behaviour methods, and they all run the rules first.</li>
 *   <li><b>Lifecycle guard</b> — every mutation calls {@link #requireActive()}; once {@link #checkout()}
 *       flips the cart to {@link CartStatus#CHECKED_OUT}, it is frozen.</li>
 *   <li><b>Line rules</b> — re-adding a SKU merges into the existing line instead of duplicating it;
 *       a line can't exceed {@link CartPolicy#MAX_QUANTITY_PER_LINE}; a cart can't exceed
 *       {@link CartPolicy#MAX_DISTINCT_LINES} distinct SKUs.</li>
 *   <li><b>Coupon rules</b> — at most one coupon; it can't be applied to an empty cart; it must clear
 *       its minimum-spend gate at apply time.</li>
 *   <li><b>Pricing</b> — {@link #totals()} derives subtotal, discount (delegated to the {@link Coupon})
 *       and shipping (free past {@link CartPolicy#FREE_SHIPPING_THRESHOLD}) on demand; nothing priced is
 *       ever stored, so totals can't go stale.</li>
 * </ul>
 *
 * <p>The model carries no JPA/Spring/serialization annotations; the persistence adapter maps to/from it.
 */
@Getter
public final class Cart {

    private final UUID id;
    private final String customerName;
    private CartStatus status;
    private final List<CartLine> lines;
    private Coupon coupon; // nullable — a cart may have no coupon

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;
    private final String updatedBy;

    private Cart(UUID id, String customerName, CartStatus status, List<CartLine> lines, Coupon coupon,
                 LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.customerName = customerName;
        this.status = status;
        this.lines = lines;
        this.coupon = coupon;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    /**
     * Factory for a brand-new, empty, {@link CartStatus#ACTIVE} cart. The only way to mint a cart; it
     * has no id yet (the persistence adapter assigns one on save).
     */
    public static Cart create(String customerName) {
        if (customerName == null || customerName.isBlank()) {
            throw new InvalidCartException("CART_CUSTOMER_NAME_REQUIRED", "Customer name is required.");
        }
        return new Cart(null, customerName.strip(), CartStatus.ACTIVE, new ArrayList<>(), null,
                null, null, null, null);
    }

    /**
     * Rebuilds a cart from stored state. Used by the persistence adapter only — it trusts the stored
     * lines/coupon/status rather than re-running the add/apply rules (so a historical cart loads even
     * if the policy limits have since tightened).
     */
    public static Cart fromPersistence(UUID id, String customerName, CartStatus status, List<CartLine> lines,
                                       Coupon coupon, LocalDateTime createdAt, LocalDateTime updatedAt,
                                       String createdBy, String updatedBy) {
        return new Cart(id, customerName, status, new ArrayList<>(lines), coupon,
                createdAt, updatedAt, createdBy, updatedBy);
    }

    // -----------------------------------------------------------------------------------------------
    // Line behaviour
    // -----------------------------------------------------------------------------------------------

    /**
     * Adds {@code quantity} of a product. If the SKU is already in the cart, the quantities are merged
     * into the existing line (no duplicate line); otherwise a new line is appended. Either way the
     * resulting line must stay within {@link CartPolicy#MAX_QUANTITY_PER_LINE}, and a brand-new SKU must
     * not push the cart past {@link CartPolicy#MAX_DISTINCT_LINES}.
     *
     * @throws CartOperationException if the cart isn't ACTIVE, or a line/cart limit would be exceeded
     */
    public void addItem(String sku, String productName, BigDecimal unitPrice, Integer quantity) {
        requireActive();
        CartLine incoming = CartLine.of(sku, productName, unitPrice, quantity);

        Optional<CartLine> existing = findLine(incoming.getSku());
        if (existing.isPresent()) {
            CartLine merged = existing.get().increasedBy(incoming.getQuantity());
            requireWithinLineLimit(merged);
            replaceLine(merged);
            return;
        }

        if (lines.size() >= CartPolicy.MAX_DISTINCT_LINES) {
            throw new CartOperationException("CART_TOO_MANY_LINES",
                    "Cart cannot hold more than " + CartPolicy.MAX_DISTINCT_LINES + " distinct items.");
        }
        requireWithinLineLimit(incoming);
        lines.add(incoming);
    }

    /**
     * Sets a line's quantity to an exact value. A quantity of {@code 0} removes the line entirely
     * (a natural "set the stepper to zero" gesture); any other value must be a valid quantity within
     * the per-line limit.
     *
     * @throws CartLineNotFoundException if the SKU isn't in the cart
     * @throws CartOperationException    if the cart isn't ACTIVE or the new quantity exceeds the limit
     */
    public void changeQuantity(String sku, Integer newQuantity) {
        requireActive();
        Sku target = Sku.of(sku);
        CartLine line = findLine(target).orElseThrow(() -> new CartLineNotFoundException(target.value()));

        if (newQuantity != null && newQuantity == 0) {
            lines.remove(line);
            return;
        }
        CartLine updated = line.withQuantity(Quantity.of(newQuantity));
        requireWithinLineLimit(updated);
        replaceLine(updated);
    }

    /**
     * Removes a line by SKU.
     *
     * @throws CartLineNotFoundException if the SKU isn't in the cart
     * @throws CartOperationException    if the cart isn't ACTIVE
     */
    public void removeItem(String sku) {
        requireActive();
        Sku target = Sku.of(sku);
        CartLine line = findLine(target).orElseThrow(() -> new CartLineNotFoundException(target.value()));
        lines.remove(line);
    }

    // -----------------------------------------------------------------------------------------------
    // Coupon behaviour
    // -----------------------------------------------------------------------------------------------

    /**
     * Applies a coupon. Exactly one coupon at a time, never to an empty cart, and only if the cart's
     * current subtotal clears the coupon's minimum-spend gate.
     *
     * @throws CartOperationException if the cart isn't ACTIVE, is empty, already has a coupon, or the
     *                                minimum-spend gate isn't met
     */
    public void applyCoupon(String code, CouponType type, BigDecimal value,
                            BigDecimal minSubtotal, BigDecimal maxDiscount) {
        requireActive();
        if (lines.isEmpty()) {
            throw new CartOperationException("CART_COUPON_EMPTY_CART",
                    "Cannot apply a coupon to an empty cart.");
        }
        if (coupon != null) {
            throw new CartOperationException("CART_COUPON_ALREADY_APPLIED",
                    "A coupon is already applied; remove it before applying another.");
        }
        Coupon candidate = Coupon.of(code, type, value, minSubtotal, maxDiscount);
        Money subtotal = subtotal();
        if (!candidate.isEligibleFor(subtotal)) {
            throw new CartOperationException("CART_COUPON_MIN_SUBTOTAL",
                    "Coupon '" + candidate.getCode() + "' needs a subtotal of at least "
                            + candidate.getMinSubtotal() + " but the cart is " + subtotal + ".");
        }
        this.coupon = candidate;
    }

    /** Removes any applied coupon. No-op if none is applied. */
    public void removeCoupon() {
        requireActive();
        this.coupon = null;
    }

    // -----------------------------------------------------------------------------------------------
    // Checkout
    // -----------------------------------------------------------------------------------------------

    /**
     * Locks the cart for ordering: a non-empty ACTIVE cart becomes {@link CartStatus#CHECKED_OUT} and
     * accepts no further changes. (In a fuller system this is the seam where an {@code Order} would be
     * created from the cart's {@link #totals()} — see the order feature.)
     *
     * @throws CartOperationException if the cart isn't ACTIVE or is empty
     */
    public void checkout() {
        requireActive();
        if (lines.isEmpty()) {
            throw new CartOperationException("CART_CHECKOUT_EMPTY", "Cannot check out an empty cart.");
        }
        this.status = CartStatus.CHECKED_OUT;
    }

    // -----------------------------------------------------------------------------------------------
    // Pricing (pure, derived — never stored)
    // -----------------------------------------------------------------------------------------------

    /**
     * Prices the cart from its current lines and coupon: subtotal, discount, shipping and grand total.
     *
     * <ul>
     *   <li><b>discount</b> — delegated to the {@link Coupon} (zero when there is none, and zero if the
     *       subtotal has since dropped below the coupon's minimum, so an item removal silently
     *       deactivates a no-longer-qualifying coupon);</li>
     *   <li><b>shipping</b> — free once the <i>post-discount</i> subtotal reaches
     *       {@link CartPolicy#FREE_SHIPPING_THRESHOLD}, otherwise a flat fee; an empty cart never
     *       incurs shipping.</li>
     * </ul>
     */
    public CartTotals totals() {
        Money subtotal = subtotal();
        Money discount = coupon == null ? Money.zero() : coupon.discountFor(subtotal);
        Money shipping = shippingFor(subtotal, discount);
        return CartTotals.of(subtotal, discount, shipping);
    }

    public Money subtotal() {
        return lines.stream()
                .map(CartLine::lineTotal)
                .reduce(Money.zero(), Money::plus);
    }

    private Money shippingFor(Money subtotal, Money discount) {
        if (lines.isEmpty()) {
            return Money.zero();
        }
        Money payable = subtotal.minus(discount);
        return payable.isGreaterThanOrEqualTo(CartPolicy.FREE_SHIPPING_THRESHOLD)
                ? Money.zero()
                : CartPolicy.FLAT_SHIPPING_FEE;
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public boolean isCheckedOut() {
        return status == CartStatus.CHECKED_OUT;
    }

    /** Defensive copy — callers can read the lines but never mutate the aggregate's internal list. */
    public List<CartLine> getLines() {
        return List.copyOf(lines);
    }

    // -----------------------------------------------------------------------------------------------
    // Internals
    // -----------------------------------------------------------------------------------------------

    private void requireActive() {
        if (status != CartStatus.ACTIVE) {
            throw new CartOperationException("CART_NOT_ACTIVE",
                    "Cart " + id + " is " + status + " and can no longer be modified.");
        }
    }

    private void requireWithinLineLimit(CartLine line) {
        if (line.getQuantity().isGreaterThan(CartPolicy.MAX_QUANTITY_PER_LINE)) {
            throw new CartOperationException("CART_LINE_QUANTITY_LIMIT",
                    "A single line may hold at most " + CartPolicy.MAX_QUANTITY_PER_LINE
                            + " units (SKU " + line.getSku() + ").");
        }
    }

    private Optional<CartLine> findLine(Sku sku) {
        return lines.stream().filter(l -> l.hasSku(sku)).findFirst();
    }

    private void replaceLine(CartLine updated) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).hasSku(updated.getSku())) {
                lines.set(i, updated);
                return;
            }
        }
    }
}
