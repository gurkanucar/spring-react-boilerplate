package com.gucardev.springreactboilerplate.features.scheduledevent.adapter.in.web;

import com.gucardev.springreactboilerplate.features.scheduledevent.adapter.in.web.dto.ScheduledEventResponse;
import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from the {@link ScheduledEvent} domain model to its web response DTO. Unmapped
 * target properties are ignored (e.g. BaseDto's deletedAt/deletedBy, which have no domain counterpart).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduledEventWebMapper {

    ScheduledEventResponse toResponse(ScheduledEvent event);
}
