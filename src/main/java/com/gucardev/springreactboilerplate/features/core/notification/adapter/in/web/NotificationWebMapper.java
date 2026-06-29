package com.gucardev.springreactboilerplate.features.core.notification.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.notification.adapter.in.web.dto.NotificationResponse;
import com.gucardev.springreactboilerplate.features.core.notification.domain.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from the {@link Notification} domain model to its web response DTO. Unmapped target
 * properties are ignored (e.g. BaseDto's deletedAt/deletedBy, which have no domain counterpart).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationWebMapper {

    NotificationResponse toResponse(Notification notification);
}
