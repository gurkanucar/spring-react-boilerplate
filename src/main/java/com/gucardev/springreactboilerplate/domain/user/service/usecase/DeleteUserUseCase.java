package com.gucardev.springreactboilerplate.domain.user.service.usecase;

import com.gucardev.springreactboilerplate.domain.user.repository.UserRepository;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteUserUseCase {

    private final UserFinder finder;
    private final UserRepository repository;

    @CacheEvict(cacheNames = CacheNames.USERS, cacheManager = CacheManagers.CAFFEINE_1M, allEntries = true)
    @Transactional
    public void execute(UUID id) {
        repository.delete(finder.findById(id));
    }
}
