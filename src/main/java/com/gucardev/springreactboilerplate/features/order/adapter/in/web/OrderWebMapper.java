package com.gucardev.springreactboilerplate.features.order.adapter.in.web;

import com.gucardev.springreactboilerplate.features.order.adapter.in.web.dto.OrderResponse;
import com.gucardev.springreactboilerplate.features.order.domain.model.Money;
import com.gucardev.springreactboilerplate.features.order.domain.model.Order;
import com.gucardev.springreactboilerplate.features.order.domain.model.Quantity;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from the {@link Order} domain model to its web response DTO. The value objects
 * ({@link Quantity}, {@link Money}) are unwrapped to their primitive wire representation via the
 * {@code map(...)} helpers below. Unmapped target properties are ignored (e.g. BaseDto's
 * deletedAt/deletedBy, which have no domain counterpart).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderWebMapper {

    OrderResponse toResponse(Order order);

    default Integer map(Quantity quantity) {
        return quantity == null ? null : quantity.value();
    }

    default BigDecimal map(Money amount) {
        return amount == null ? null : amount.value();
    }
}
