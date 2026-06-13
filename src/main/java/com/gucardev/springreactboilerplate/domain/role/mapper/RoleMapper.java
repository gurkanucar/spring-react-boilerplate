package com.gucardev.springreactboilerplate.domain.role.mapper;

import com.gucardev.springreactboilerplate.domain.role.entity.Role;
import com.gucardev.springreactboilerplate.domain.role.model.dto.RoleResponseDto;
import com.gucardev.springreactboilerplate.domain.role.model.request.CreateRoleRequest;
import com.gucardev.springreactboilerplate.domain.role.model.request.UpdateRoleRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between {@link Role} and its DTOs. Null request fields are skipped on update
 * so partial updates don't wipe columns.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

    RoleResponseDto toDto(Role role);

    @Mapping(target = "id", ignore = true)
    Role toEntity(CreateRoleRequest request);

    void updateEntity(UpdateRoleRequest request, @MappingTarget Role role);
}
