package com.gucardev.springreactboilerplate.features.order.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.order.application.port.out.LoadOrderPort;
import com.gucardev.springreactboilerplate.features.order.application.port.out.SaveOrderPort;
import com.gucardev.springreactboilerplate.features.order.domain.model.Order;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the order load/save output ports with Spring Data JPA. Maps domain ⇄ entity
 * at the boundary so the application core stays persistence-agnostic.
 */
@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements LoadOrderPort, SaveOrderPort {

    private final OrderJpaRepository repository;
    private final OrderPersistenceMapper mapper;

    @Override
    public Optional<Order> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Order save(Order order) {
        return mapper.toDomain(repository.save(mapper.toEntity(order)));
    }
}
