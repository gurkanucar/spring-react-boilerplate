package com.gucardev.springreactboilerplate.features.core.role.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.role.adapter.in.web.dto.RoleResponseDto;
import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from the {@link Role} domain model to its web response DTO. Unmapped target
 * properties are ignored (e.g. BaseDto's deletedAt/deletedBy, which have no domain counterpart).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleWebMapper {

    RoleResponseDto toResponse(Role role);
}
