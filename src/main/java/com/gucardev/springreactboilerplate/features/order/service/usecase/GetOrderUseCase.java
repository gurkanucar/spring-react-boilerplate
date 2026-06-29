package com.gucardev.springreactboilerplate.features.order.service.usecase;

import com.gucardev.springreactboilerplate.features.order.mapper.OrderMapper;
import com.gucardev.springreactboilerplate.features.order.model.dto.OrderResponse;
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
public class GetOrderUseCase {

    private final OrderFinder finder;
    private final OrderMapper mapper;

    @Transactional(readOnly = true)
    public OrderResponse execute(UUID id) {
        return mapper.toDto(finder.findById(id));
    }
}
