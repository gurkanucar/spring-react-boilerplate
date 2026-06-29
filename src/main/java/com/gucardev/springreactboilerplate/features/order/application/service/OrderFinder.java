package com.gucardev.springreactboilerplate.features.order.application.service;

import com.gucardev.springreactboilerplate.features.order.application.exception.OrderExceptionType;
import com.gucardev.springreactboilerplate.features.order.application.port.out.LoadOrderPort;
import com.gucardev.springreactboilerplate.features.order.domain.model.Order;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared "fetch or 404" lookup for orders, used by the read-side use cases.
 */
@Service
@RequiredArgsConstructor
public class OrderFinder {

    private final LoadOrderPort loadOrderPort;

    public Order findById(UUID id) {
        return loadOrderPort.findById(id)
                .orElseThrow(() -> OrderExceptionType.NOT_FOUND.toException(id));
    }
}
