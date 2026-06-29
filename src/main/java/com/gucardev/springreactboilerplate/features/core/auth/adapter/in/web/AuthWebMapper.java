package com.gucardev.springreactboilerplate.features.core.auth.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.auth.adapter.in.web.dto.TokenResponseDto;
import com.gucardev.springreactboilerplate.features.core.auth.domain.model.AuthTokens;
import com.gucardev.springreactboilerplate.features.core.user.adapter.in.web.UserWebMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from the {@link AuthTokens} domain model to the token response DTO. The nested
 * user is mapped via the user feature's {@link UserWebMapper} (the same domain {@code User} ->
 * {@code UserResponseDto} mapping used by the user endpoints).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = UserWebMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthWebMapper {

    TokenResponseDto toResponse(AuthTokens tokens);
}
