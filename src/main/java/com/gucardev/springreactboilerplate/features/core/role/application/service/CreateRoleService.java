package com.gucardev.springreactboilerplate.features.core.role.application.service;

import com.gucardev.springreactboilerplate.features.core.role.application.exception.RoleExceptionType;
import com.gucardev.springreactboilerplate.features.core.role.application.port.in.CreateRoleCommand;
import com.gucardev.springreactboilerplate.features.core.role.application.port.in.CreateRoleUseCase;
import com.gucardev.springreactboilerplate.features.core.role.application.port.out.LoadRolePort;
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
public class CreateRoleService implements CreateRoleUseCase {

    private final LoadRolePort loadRolePort;
    private final SaveRolePort saveRolePort;

    @Override
    @CacheEvict(cacheNames = CacheNames.ROLES, cacheManager = CacheManagers.CAFFEINE_30M, allEntries = true)
    @Transactional
    public Role create(CreateRoleCommand command) {
        if (loadRolePort.existsByName(command.name())) {
            throw RoleExceptionType.NAME_ALREADY_EXISTS.toException(command.name());
        }
        return saveRolePort.save(Role.builder()
                .name(command.name())
                .displayName(command.displayName())
                .description(command.description())
                .build());
    }
}
