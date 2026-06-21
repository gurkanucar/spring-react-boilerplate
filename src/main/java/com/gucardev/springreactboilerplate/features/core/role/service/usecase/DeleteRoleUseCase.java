package com.gucardev.springreactboilerplate.features.core.role.service.usecase;

import com.gucardev.springreactboilerplate.features.core.role.repository.RoleRepository;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteRoleUseCase {

    private final RoleFinder finder;
    private final RoleRepository repository;

    @CacheEvict(cacheNames = CacheNames.ROLES, cacheManager = CacheManagers.CAFFEINE_30M, allEntries = true)
    @Transactional
    public void execute(Long id) {
        repository.delete(finder.findById(id));
    }
}
