package com.gucardev.springreactboilerplate.domain.user.mapper;

import com.gucardev.springreactboilerplate.domain.user.entity.Role;
import com.gucardev.springreactboilerplate.domain.user.entity.User;
import com.gucardev.springreactboilerplate.domain.user.model.dto.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from {@link User} to its response DTO. The {@code Set<Role>} is flattened to
 * a {@code Set<String>} of role names via {@link #roleName(Role)}; unmapped targets are ignored.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserResponseDto toDto(User user);

    default String roleName(Role role) {
        return role.getName();
    }
}
