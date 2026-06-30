package com.gucardev.springreactboilerplate.features.cart.application.service;

import com.gucardev.springreactboilerplate.features.cart.application.port.in.GetCartUseCase;
import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reads a single cart. The web mapper prices it out via {@code cart.totals()} on the way to the DTO.
 */
@Service
@RequiredArgsConstructor
public class GetCartService implements GetCartUseCase {

    private final CartFinder finder;

    @Override
    @Transactional(readOnly = true)
    public Cart getById(UUID id) {
        return finder.findById(id);
    }
}
