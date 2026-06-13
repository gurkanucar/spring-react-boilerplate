package com.gucardev.springreactboilerplate.domain.user.service.usecase;

import com.gucardev.springreactboilerplate.domain.user.entity.User;
import com.gucardev.springreactboilerplate.domain.user.mapper.UserMapper;
import com.gucardev.springreactboilerplate.domain.user.model.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.domain.user.model.request.UpdateUserRequest;
import com.gucardev.springreactboilerplate.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Admin user update: applies non-null profile/status fields and, when {@code roles} is provided,
 * replaces the user's role set.
 */
@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final UserFinder finder;
    private final UserRepository repository;
    private final UserMapper mapper;
    private final UserRoleResolver roleResolver;

    @Transactional
    public UserResponseDto execute(UUID id, UpdateUserRequest request) {
        User user = finder.findById(id);
        mapper.updateEntity(request, user);
        // Status flags handled explicitly (record `isActive()` accessor is ambiguous for MapStruct).
        if (request.activated() != null) {
            user.setActivated(request.activated());
        }
        if (request.isActive() != null) {
            user.setIsActive(request.isActive());
        }
        if (request.roles() != null) {
            user.setRoles(roleResolver.resolve(request.roles()));
        }
        return mapper.toDto(repository.save(user));
    }
}
