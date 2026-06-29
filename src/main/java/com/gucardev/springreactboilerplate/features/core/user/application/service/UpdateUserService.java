package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.user.application.port.in.UpdateUserCommand;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.UpdateUserUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.SaveUserPort;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
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
 * effect on the next request. Returns the updated user enriched with its profile-image URLs.
 */
@Service
@RequiredArgsConstructor
public class UpdateUserService implements UpdateUserUseCase {

    private final UserFinder finder;
    private final SaveUserPort saveUserPort;
    private final UserRoleResolver roleResolver;
    private final UserTenantAssignmentValidator tenantAssignmentValidator;
    private final UserImageEnricher imageEnricher;

    @Override
    @CacheEvict(cacheNames = CacheNames.USERS, cacheManager = CacheManagers.CAFFEINE_1M, allEntries = true)
    @Transactional
    public User update(UUID id, UpdateUserCommand command) {
        User user = finder.findById(id);
        if (command.name() != null) {
            user.setName(command.name());
        }
        if (command.surname() != null) {
            user.setSurname(command.surname());
        }
        if (command.phoneNumber() != null) {
            user.setPhoneNumber(command.phoneNumber());
        }
        if (command.activated() != null) {
            user.setActivated(command.activated());
        }
        if (command.isActive() != null) {
            user.setIsActive(command.isActive());
        }
        if (command.organizationId() != null) {
            user.setOrganizationId(command.organizationId());
        }
        if (command.workspaceId() != null) {
            user.setWorkspaceId(command.workspaceId());
        }
        if (command.roles() != null) {
            user.setRoles(roleResolver.resolve(command.roles()));
        }
        // Validate the resulting tenant assignment (org/workspace may have changed independently).
        tenantAssignmentValidator.validate(user.getOrganizationId(), user.getWorkspaceId());

        User saved = saveUserPort.save(user);
        imageEnricher.enrich(saved);
        return saved;
    }
}
