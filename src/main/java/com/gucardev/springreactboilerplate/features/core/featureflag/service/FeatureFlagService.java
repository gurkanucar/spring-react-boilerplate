package com.gucardev.springreactboilerplate.features.core.featureflag.service;

import com.gucardev.springreactboilerplate.features.core.featureflag.FeatureFlags;
import com.gucardev.springreactboilerplate.features.core.featureflag.entity.FeatureFlag;
import com.gucardev.springreactboilerplate.features.core.featureflag.model.dto.FeatureFlagDto;
import com.gucardev.springreactboilerplate.features.core.featureflag.repository.FeatureFlagRepository;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Per-workspace feature flags. The effective value of a flag is its stored override, or {@code false}
 * when no override exists. The hot read paths ({@link #isEnabled}, {@link #effectiveMap}, {@link #list})
 * are cached in-memory per workspace; any write evicts the whole bucket (toggles are rare).
 *
 * <p>The cache is in-memory ({@link CacheManagers#CAFFEINE_10M}). For a multi-instance deployment where
 * a toggle on one node must be seen immediately on the others, switch the cache manager on the methods
 * below to a {@code REDIS_*} manager.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeatureFlagService {

    private final FeatureFlagRepository repository;

    /** Effective on/off for one flag in a workspace: the stored override, else false. */
    @Cacheable(cacheNames = CacheNames.FEATURE_FLAGS, cacheManager = CacheManagers.CAFFEINE_10M,
            key = "#workspaceId + ':' + #key")
    public boolean isEnabled(UUID workspaceId, String key) {
        return repository.findByWorkspaceIdAndFlagKey(workspaceId, key)
                .map(FeatureFlag::getEnabled)
                .orElse(false);
    }

    /** Effective state of the known flags + any stored custom flags, keyed by name. */
    @Cacheable(cacheNames = CacheNames.FEATURE_FLAGS, cacheManager = CacheManagers.CAFFEINE_10M,
            key = "#workspaceId + ':map'")
    public Map<String, Boolean> effectiveMap(UUID workspaceId) {
        Map<String, Boolean> stored = new LinkedHashMap<>();
        repository.findByWorkspaceId(workspaceId).forEach(f -> stored.put(f.getFlagKey(), f.getEnabled()));
        Map<String, Boolean> result = new LinkedHashMap<>();
        FeatureFlags.KNOWN.forEach(key -> result.put(key, stored.getOrDefault(key, false)));
        stored.forEach(result::putIfAbsent); // include any custom flags not in the catalog
        return result;
    }

    /** Admin view: known flags + any stored custom flags, each with its effective value. */
    @Cacheable(cacheNames = CacheNames.FEATURE_FLAGS, cacheManager = CacheManagers.CAFFEINE_10M,
            key = "#workspaceId + ':list'")
    public List<FeatureFlagDto> list(UUID workspaceId) {
        Map<String, FeatureFlag> stored = new LinkedHashMap<>();
        repository.findByWorkspaceId(workspaceId).forEach(f -> stored.put(f.getFlagKey(), f));

        Map<String, FeatureFlagDto> out = new LinkedHashMap<>();
        for (String key : FeatureFlags.KNOWN) {
            FeatureFlag row = stored.get(key);
            out.put(key, FeatureFlagDto.builder()
                    .key(key)
                    .enabled(row != null && Boolean.TRUE.equals(row.getEnabled()))
                    .isDefault(row == null)
                    .build());
        }
        stored.forEach((key, row) -> out.putIfAbsent(key, FeatureFlagDto.builder()
                .key(key)
                .enabled(Boolean.TRUE.equals(row.getEnabled()))
                .isDefault(false)
                .build()));
        return List.copyOf(out.values());
    }

    /** Enable every known flag for a workspace — used when a workspace is first created. */
    @CacheEvict(cacheNames = CacheNames.FEATURE_FLAGS, cacheManager = CacheManagers.CAFFEINE_10M,
            allEntries = true)
    @Transactional
    public void enableDefaults(UUID workspaceId) {
        FeatureFlags.KNOWN.forEach(key -> upsert(workspaceId, key, true));
    }

    /** Upsert the per-workspace override for a flag (any string key). */
    @CacheEvict(cacheNames = CacheNames.FEATURE_FLAGS, cacheManager = CacheManagers.CAFFEINE_10M,
            allEntries = true)
    @Transactional
    public FeatureFlagDto set(UUID workspaceId, String key, boolean enabled) {
        upsert(workspaceId, key, enabled);
        log.info("Feature flag '{}' set to {} for workspace {}", key, enabled, workspaceId);
        return FeatureFlagDto.builder().key(key).enabled(enabled).isDefault(false).build();
    }

    /** Remove every flag override for a workspace — used when the workspace is deleted. */
    @CacheEvict(cacheNames = CacheNames.FEATURE_FLAGS, cacheManager = CacheManagers.CAFFEINE_10M,
            allEntries = true)
    @Transactional
    public void deleteForWorkspace(UUID workspaceId) {
        repository.deleteByWorkspaceId(workspaceId);
    }

    private void upsert(UUID workspaceId, String key, boolean enabled) {
        FeatureFlag flag = repository.findByWorkspaceIdAndFlagKey(workspaceId, key)
                .orElseGet(() -> FeatureFlag.builder().workspaceId(workspaceId).flagKey(key).build());
        flag.setEnabled(enabled);
        repository.save(flag);
    }
}
