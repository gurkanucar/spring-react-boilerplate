package com.gucardev.springreactboilerplate.features.cart.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.cart.application.port.out.LoadCartPort;
import com.gucardev.springreactboilerplate.features.cart.application.port.out.SaveCartPort;
import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the cart load/save output ports with Spring Data JPA. Maps domain ⇄ entity at
 * the boundary so the application core stays persistence-agnostic.
 */
@Component
@RequiredArgsConstructor
public class CartPersistenceAdapter implements LoadCartPort, SaveCartPort {

    private final CartJpaRepository repository;
    private final CartPersistenceMapper mapper;

    @Override
    public Optional<Cart> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Cart save(Cart cart) {
        return mapper.toDomain(repository.save(mapper.toEntity(cart)));
    }
}
