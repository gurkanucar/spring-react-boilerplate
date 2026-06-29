package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.user.application.port.in.DeleteUserUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.DeleteUserPort;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteUserService implements DeleteUserUseCase {

    private final UserFinder finder;
    private final DeleteUserPort deleteUserPort;

    @Override
    @CacheEvict(cacheNames = CacheNames.USERS, cacheManager = CacheManagers.CAFFEINE_1M, allEntries = true)
    @Transactional
    public void delete(UUID id) {
        deleteUserPort.delete(finder.findById(id));
    }
}
