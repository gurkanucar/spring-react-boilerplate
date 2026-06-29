package com.gucardev.springreactboilerplate.features.core.featureflag.adapter.out.cache;

import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.out.FeatureFlagCachePort;
import com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlagState;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Driven cache adapter for the per-workspace feature-flag read paths. Backs the
 * {@link FeatureFlagCachePort} with Spring's declarative caching: each method caches its loader's
 * result under a workspace-scoped key, and any write evicts the whole bucket (toggles are rare).
 *
 * <p>The cache is in-memory ({@link CacheManagers#CAFFEINE_10M}). For a multi-instance deployment
 * where a toggle on one node must be seen immediately on the others, switch the cache manager on the
 * methods below to a {@code REDIS_*} manager.
 */
@Component
public class FeatureFlagCacheAdapter implements FeatureFlagCachePort {

    @Override
    @Cacheable(cacheNames = CacheNames.FEATURE_FLAGS, cacheManager = CacheManagers.CAFFEINE_10M,
            key = "#workspaceId + ':' + #key")
    public boolean isEnabled(UUID workspaceId, String key, BooleanSupplier loader) {
        return loader.getAsBoolean();
    }

    @Override
    @Cacheable(cacheNames = CacheNames.FEATURE_FLAGS, cacheManager = CacheManagers.CAFFEINE_10M,
            key = "#workspaceId + ':map'")
    public Map<String, Boolean> effectiveMap(UUID workspaceId, Supplier<Map<String, Boolean>> loader) {
        return loader.get();
    }

    @Override
    @Cacheable(cacheNames = CacheNames.FEATURE_FLAGS, cacheManager = CacheManagers.CAFFEINE_10M,
            key = "#workspaceId + ':list'")
    public List<FeatureFlagState> list(UUID workspaceId, Supplier<List<FeatureFlagState>> loader) {
        return loader.get();
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.FEATURE_FLAGS, cacheManager = CacheManagers.CAFFEINE_10M,
            allEntries = true)
    public void evictAll() {
    }
}
