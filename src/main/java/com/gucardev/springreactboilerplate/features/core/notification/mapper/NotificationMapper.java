package com.gucardev.springreactboilerplate.features.core.notification.mapper;

import com.gucardev.springreactboilerplate.features.core.notification.entity.Notification;
import com.gucardev.springreactboilerplate.features.core.notification.model.dto.NotificationDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

    NotificationDto toDto(Notification notification);
}
