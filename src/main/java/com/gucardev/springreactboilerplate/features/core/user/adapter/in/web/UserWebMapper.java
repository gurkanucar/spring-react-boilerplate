package com.gucardev.springreactboilerplate.features.core.user.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import com.gucardev.springreactboilerplate.features.core.user.adapter.in.web.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from the {@link User} domain model to its web response DTO. Roles flatten to name
 * strings; the profile-image URLs are already resolved onto the domain model by the application
 * services. Unmapped target properties are ignored (e.g. BaseDto's deletedAt/deletedBy).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserWebMapper {

    UserResponseDto toResponse(User user);

    default String roleToName(Role role) {
        return role == null ? null : role.getName();
    }
}
