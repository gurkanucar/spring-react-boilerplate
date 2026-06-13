package com.gucardev.springreactboilerplate.domain.user.mapper;

import com.gucardev.springreactboilerplate.domain.role.entity.Role;
import com.gucardev.springreactboilerplate.domain.user.entity.User;
import com.gucardev.springreactboilerplate.domain.user.model.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.domain.user.model.request.CreateUserRequest;
import com.gucardev.springreactboilerplate.domain.user.model.request.UpdateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for {@link User}. The {@code Set<Role>} is flattened to a {@code Set<String>}
 * of role names on read via {@link #roleName(Role)}. On write, {@code password} and {@code roles}
 * are deliberately left for the use case to set (password hashing, role resolution), and null
 * request fields are skipped on update so partial updates don't wipe columns.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(CreateUserRequest request);

    @Mapping(target = "roles", ignore = true)
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);

    default String roleName(Role role) {
        return role.getName();
    }
}
