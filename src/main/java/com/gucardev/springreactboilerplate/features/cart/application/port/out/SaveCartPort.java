package com.gucardev.springreactboilerplate.features.cart.application.port.out;

import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;

/**
 * Output port: persist a cart (insert or update) and return the stored state, including any generated
 * id and audit metadata.
 */
public interface SaveCartPort {

    Cart save(Cart cart);
}
