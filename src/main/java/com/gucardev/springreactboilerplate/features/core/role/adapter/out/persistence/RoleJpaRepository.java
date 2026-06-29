package com.gucardev.springreactboilerplate.features.core.role.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link RoleJpaEntity}. An implementation detail of the persistence
 * adapter — the application core never sees it, only the role output ports.
 */
@Repository
public interface RoleJpaRepository extends BaseJpaRepository<RoleJpaEntity, Long> {

    /**
     * Hot path (register + user role resolution). Roles rarely change, so the result is cached;
     * role create/update/delete evict the whole {@code roles} cache. Empty results are not cached.
     */
    // Spring unwraps the Optional before caching, so #result is the entity (or null when absent).
    @Cacheable(cacheNames = CacheNames.ROLES, cacheManager = CacheManagers.CAFFEINE_30M,
            key = "#name", unless = "#result == null")
    Optional<RoleJpaEntity> findByName(String name);

    boolean existsByName(String name);
}
