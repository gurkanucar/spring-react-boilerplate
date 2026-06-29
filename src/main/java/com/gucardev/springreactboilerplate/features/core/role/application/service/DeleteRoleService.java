package com.gucardev.springreactboilerplate.features.core.role.application.service;

import com.gucardev.springreactboilerplate.features.core.role.application.port.in.DeleteRoleUseCase;
import com.gucardev.springreactboilerplate.features.core.role.application.port.out.DeleteRolePort;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteRoleService implements DeleteRoleUseCase {

    private final RoleFinder finder;
    private final DeleteRolePort deleteRolePort;

    @Override
    @CacheEvict(cacheNames = CacheNames.ROLES, cacheManager = CacheManagers.CAFFEINE_30M, allEntries = true)
    @Transactional
    public void delete(Long id) {
        deleteRolePort.delete(finder.findById(id));
    }
}
