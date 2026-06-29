package com.gucardev.springreactboilerplate.features.order.service.usecase;

import com.gucardev.springreactboilerplate.features.order.entity.Order;
import com.gucardev.springreactboilerplate.features.order.exception.OrderExceptionType;
import com.gucardev.springreactboilerplate.features.order.repository.OrderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared "fetch or 404" lookup for orders.
 */
@Service
@RequiredArgsConstructor
public class OrderFinder {

    private final OrderRepository repository;

    public Order findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> OrderExceptionType.NOT_FOUND.toException(id));
    }
}
