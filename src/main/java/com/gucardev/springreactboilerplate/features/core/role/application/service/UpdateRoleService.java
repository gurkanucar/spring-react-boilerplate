package com.gucardev.springreactboilerplate.features.core.role.application.service;

import com.gucardev.springreactboilerplate.features.core.role.application.port.in.UpdateRoleCommand;
import com.gucardev.springreactboilerplate.features.core.role.application.port.in.UpdateRoleUseCase;
import com.gucardev.springreactboilerplate.features.core.role.application.port.out.SaveRolePort;
import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateRoleService implements UpdateRoleUseCase {

    private final RoleFinder finder;
    private final SaveRolePort saveRolePort;

    @Override
    @CacheEvict(cacheNames = CacheNames.ROLES, cacheManager = CacheManagers.CAFFEINE_30M, allEntries = true)
    @Transactional
    public Role update(Long id, UpdateRoleCommand command) {
        Role role = finder.findById(id);
        role.updateDetails(command.displayName(), command.description());
        return saveRolePort.save(role);
    }
}
