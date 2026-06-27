package com.gucardev.springreactboilerplate.features.scheduledevent.mapper;

import com.gucardev.springreactboilerplate.features.scheduledevent.entity.ScheduledEvent;
import com.gucardev.springreactboilerplate.features.scheduledevent.model.dto.ScheduledEventResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from {@link ScheduledEvent} to its response DTO. Unmapped target properties are
 * ignored (e.g. BaseDto's deletedAt/deletedBy, which have no entity counterpart).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduledEventMapper {

    ScheduledEventResponse toDto(ScheduledEvent entity);
}
