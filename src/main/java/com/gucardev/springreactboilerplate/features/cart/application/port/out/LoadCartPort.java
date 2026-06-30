package com.gucardev.springreactboilerplate.features.cart.application.port.out;

import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port: load a cart from the store. Implemented by a driven persistence adapter.
 */
public interface LoadCartPort {

    Optional<Cart> findById(UUID id);
}
