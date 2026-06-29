package com.gucardev.springreactboilerplate.features.order.mapper;

import com.gucardev.springreactboilerplate.features.order.entity.Order;
import com.gucardev.springreactboilerplate.features.order.model.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from {@link Order} to its response DTO. Unmapped target properties are ignored
 * (e.g. BaseDto's deletedAt/deletedBy, which have no entity counterpart).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    OrderResponse toDto(Order entity);
}
