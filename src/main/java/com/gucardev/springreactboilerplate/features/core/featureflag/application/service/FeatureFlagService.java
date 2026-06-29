package com.gucardev.springreactboilerplate.features.core.featureflag.application.service;

import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in.DeleteWorkspaceFeatureFlagsUseCase;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in.GetWorkspaceFeatureFlagsUseCase;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in.IsFeatureEnabledUseCase;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in.SeedWorkspaceFeatureFlagsUseCase;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in.UpdateFeatureFlagCommand;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in.UpdateFeatureFlagUseCase;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.out.DeleteFeatureFlagPort;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.out.FeatureFlagCachePort;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.out.LoadFeatureFlagPort;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.out.SaveFeatureFlagPort;
import com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlag;
import com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlagState;
import com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlags;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Per-workspace feature flags. The effective value of a flag is its stored override, or {@code false}
 * when no override exists. The hot read paths ({@link #isEnabled}, {@link #effectiveFlags},
 * {@link #getForCurrentWorkspace}) are cached per workspace via the {@link FeatureFlagCachePort}; any
 * write evicts the whole bucket (toggles are rare).
 *
 * <p>This single application service implements every feature-flag use case, depending only on the
 * domain model and the output ports.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeatureFlagService implements
        IsFeatureEnabledUseCase,
        GetWorkspaceFeatureFlagsUseCase,
        UpdateFeatureFlagUseCase,
        SeedWorkspaceFeatureFlagsUseCase,
        DeleteWorkspaceFeatureFlagsUseCase {

    private final LoadFeatureFlagPort loadFeatureFlagPort;
    private final SaveFeatureFlagPort saveFeatureFlagPort;
    private final DeleteFeatureFlagPort deleteFeatureFlagPort;
    private final FeatureFlagCachePort cachePort;

    /** Effective on/off for one flag in a workspace: the stored override, else false. */
    @Override
    public boolean isEnabled(UUID workspaceId, String key) {
        return cachePort.isEnabled(workspaceId, key, () ->
                loadFeatureFlagPort.findByWorkspaceIdAndFlagKey(workspaceId, key)
                        .map(FeatureFlag::getEnabled)
                        .orElse(false));
    }

    /** Effective state of the known flags + any stored custom flags, keyed by name. */
    @Override
    public Map<String, Boolean> effectiveFlags(UUID workspaceId) {
        return cachePort.effectiveMap(workspaceId, () -> {
            Map<String, Boolean> stored = new LinkedHashMap<>();
            loadFeatureFlagPort.findByWorkspaceId(workspaceId)
                    .forEach(f -> stored.put(f.getFlagKey(), f.getEnabled()));
            Map<String, Boolean> result = new LinkedHashMap<>();
            FeatureFlags.KNOWN.forEach(key -> result.put(key, stored.getOrDefault(key, false)));
            stored.forEach(result::putIfAbsent); // include any custom flags not in the catalog
            return result;
        });
    }

    /** Admin view: known flags + any stored custom flags, each with its effective value. */
    @Override
    public List<FeatureFlagState> getForCurrentWorkspace() {
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        return cachePort.list(workspaceId, () -> buildList(workspaceId));
    }

    private List<FeatureFlagState> buildList(UUID workspaceId) {
        Map<String, FeatureFlag> stored = new LinkedHashMap<>();
        loadFeatureFlagPort.findByWorkspaceId(workspaceId).forEach(f -> stored.put(f.getFlagKey(), f));

        Map<String, FeatureFlagState> out = new LinkedHashMap<>();
        for (String key : FeatureFlags.KNOWN) {
            FeatureFlag row = stored.get(key);
            out.put(key, FeatureFlagState.builder()
                    .key(key)
                    .enabled(row != null && Boolean.TRUE.equals(row.getEnabled()))
                    .isDefault(row == null)
                    .build());
        }
        stored.forEach((key, row) -> out.putIfAbsent(key, FeatureFlagState.builder()
                .key(key)
                .enabled(Boolean.TRUE.equals(row.getEnabled()))
                .isDefault(false)
                .build()));
        return List.copyOf(out.values());
    }

    /** Upsert the per-workspace override for a flag (any string key) for the current workspace. */
    @Override
    @Transactional
    public FeatureFlagState update(UpdateFeatureFlagCommand command) {
        UUID workspaceId = TenantContextHolder.requireWorkspaceId();
        boolean enabled = Boolean.TRUE.equals(command.enabled());
        upsert(workspaceId, command.key(), enabled);
        cachePort.evictAll();
        log.info("Feature flag '{}' set to {} for workspace {}", command.key(), enabled, workspaceId);
        return FeatureFlagState.builder().key(command.key()).enabled(enabled).isDefault(false).build();
    }

    /** Enable every known flag for a workspace — used when a workspace is first created. */
    @Override
    @Transactional
    public void seedDefaults(UUID workspaceId) {
        FeatureFlags.KNOWN.forEach(key -> upsert(workspaceId, key, true));
        cachePort.evictAll();
    }

    /** Remove every flag override for a workspace — used when the workspace is deleted. */
    @Override
    @Transactional
    public void deleteForWorkspace(UUID workspaceId) {
        deleteFeatureFlagPort.deleteByWorkspaceId(workspaceId);
        cachePort.evictAll();
    }

    private void upsert(UUID workspaceId, String key, boolean enabled) {
        FeatureFlag flag = loadFeatureFlagPort.findByWorkspaceIdAndFlagKey(workspaceId, key)
                .orElseGet(() -> FeatureFlag.builder().workspaceId(workspaceId).flagKey(key).build());
        if (enabled) {
            flag.enable();
        } else {
            flag.disable();
        }
        saveFeatureFlagPort.save(flag);
    }
}
