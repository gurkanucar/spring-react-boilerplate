package com.gucardev.springreactboilerplate.domain.user.service.usecase;

import com.gucardev.springreactboilerplate.domain.user.entity.User;
import com.gucardev.springreactboilerplate.domain.user.mapper.UserMapper;
import com.gucardev.springreactboilerplate.domain.user.model.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.domain.user.model.request.UpdateUserRequest;
import com.gucardev.springreactboilerplate.domain.user.repository.UserRepository;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Admin user update: applies non-null profile/status fields and, when {@code roles} is provided,
 * replaces the user's role set. Evicts the cached user principals so role/enabled changes take
 * effect on the next request.
 */
@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final UserFinder finder;
    private final UserRepository repository;
    private final UserMapper userMapper;
    private final UserRoleResolver roleResolver;
    private final UserTenantAssignmentValidator tenantAssignmentValidator;

    @CacheEvict(cacheNames = CacheNames.USERS, cacheManager = CacheManagers.CAFFEINE_1M, allEntries = true)
    @Transactional
    public UserResponseDto execute(UUID id, UpdateUserRequest request) {
        User user = finder.findById(id);
        userMapper.updateEntity(request, user);
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
        // Validate the resulting tenant assignment (org/workspace may have changed independently).
        tenantAssignmentValidator.validate(user.getOrganizationId(), user.getWorkspaceId());
        return userMapper.toDto(repository.save(user));
    }
}
