package com.gucardev.springreactboilerplate.features.cart.application.service;

import com.gucardev.springreactboilerplate.features.cart.application.exception.CartExceptionType;
import com.gucardev.springreactboilerplate.features.cart.application.port.out.LoadCartPort;
import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared "fetch or 404" lookup for carts, used by every cart use case (they all load, mutate, save).
 */
@Service
@RequiredArgsConstructor
public class CartFinder {

    private final LoadCartPort loadCartPort;

    public Cart findById(UUID id) {
        return loadCartPort.findById(id)
                .orElseThrow(() -> CartExceptionType.NOT_FOUND.toException(id));
    }
}
