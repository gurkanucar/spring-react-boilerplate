package com.gucardev.springreactboilerplate.features.core.role.repository;

import com.gucardev.springreactboilerplate.features.core.role.entity.Role;
import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseJpaRepository<Role, Long> {

    /**
     * Hot path (register + user role resolution). Roles rarely change, so the result is cached;
     * role create/update/delete evict the whole {@code roles} cache. Empty results are not cached.
     */
    // Spring unwraps the Optional before caching, so #result is the Role (or null when absent).
    @Cacheable(cacheNames = CacheNames.ROLES, cacheManager = CacheManagers.CAFFEINE_30M,
            key = "#name", unless = "#result == null")
    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}
