package com.gucardev.springreactboilerplate.features.order.application.service;

import com.gucardev.springreactboilerplate.features.order.application.port.in.GetOrderUseCase;
import com.gucardev.springreactboilerplate.features.order.domain.model.Order;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reads a single order — handy for watching its status flip from PLACED to CONFIRMED after the
 * consumer handles the event.
 */
@Service
@RequiredArgsConstructor
public class GetOrderService implements GetOrderUseCase {

    private final OrderFinder finder;

    @Override
    @Transactional(readOnly = true)
    public Order getById(UUID id) {
        return finder.findById(id);
    }
}
