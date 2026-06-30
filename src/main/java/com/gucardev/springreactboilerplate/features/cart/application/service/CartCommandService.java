package com.gucardev.springreactboilerplate.features.cart.application.service;

import com.gucardev.springreactboilerplate.features.cart.application.port.in.AddCartItemCommand;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.AddCartItemUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.ApplyCouponCommand;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.ApplyCouponUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.CheckoutCartUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.CreateCartCommand;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.CreateCartUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.RemoveCartItemUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.RemoveCouponUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.UpdateCartItemQuantityCommand;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.UpdateCartItemQuantityUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.out.SaveCartPort;
import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The write side of the cart feature. It implements every cart-mutating input port, and on purpose it
 * is <b>thin</b>: each method does exactly three things — load the aggregate (or create one), call a
 * single behaviour method on it, save it. There is no business logic here; it all lives in the
 * {@link Cart} aggregate and its value objects. That is the whole point of the rich-domain style — the
 * service orchestrates a transaction, the model makes the decisions.
 *
 * <p>One service implements several input ports here for the same reason the persistence adapter
 * implements several output ports: the operations share one trivial load→mutate→save shape, so
 * splitting them into a class each would be ceremony without benefit. Driving adapters still depend on
 * the narrow port interfaces, not on this class.
 */
@Service
@RequiredArgsConstructor
public class CartCommandService implements
        CreateCartUseCase,
        AddCartItemUseCase,
        UpdateCartItemQuantityUseCase,
        RemoveCartItemUseCase,
        ApplyCouponUseCase,
        RemoveCouponUseCase,
        CheckoutCartUseCase {

    private final CartFinder finder;
    private final SaveCartPort saveCartPort;

    @Override
    @Transactional
    public Cart create(CreateCartCommand command) {
        return saveCartPort.save(Cart.create(command.customerName()));
    }

    @Override
    @Transactional
    public Cart addItem(AddCartItemCommand command) {
        Cart cart = finder.findById(command.cartId());
        cart.addItem(command.sku(), command.productName(), command.unitPrice(), command.quantity());
        return saveCartPort.save(cart);
    }

    @Override
    @Transactional
    public Cart updateQuantity(UpdateCartItemQuantityCommand command) {
        Cart cart = finder.findById(command.cartId());
        cart.changeQuantity(command.sku(), command.quantity());
        return saveCartPort.save(cart);
    }

    @Override
    @Transactional
    public Cart removeItem(UUID cartId, String sku) {
        Cart cart = finder.findById(cartId);
        cart.removeItem(sku);
        return saveCartPort.save(cart);
    }

    @Override
    @Transactional
    public Cart applyCoupon(ApplyCouponCommand command) {
        Cart cart = finder.findById(command.cartId());
        cart.applyCoupon(command.code(), command.type(), command.value(),
                command.minSubtotal(), command.maxDiscount());
        return saveCartPort.save(cart);
    }

    @Override
    @Transactional
    public Cart removeCoupon(UUID cartId) {
        Cart cart = finder.findById(cartId);
        cart.removeCoupon();
        return saveCartPort.save(cart);
    }

    @Override
    @Transactional
    public Cart checkout(UUID cartId) {
        Cart cart = finder.findById(cartId);
        cart.checkout();
        return saveCartPort.save(cart);
    }
}
