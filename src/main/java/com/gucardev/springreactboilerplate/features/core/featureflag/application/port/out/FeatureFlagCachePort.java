package com.gucardev.springreactboilerplate.features.core.featureflag.application.port.out;

import com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlagState;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Output port for the per-workspace feature-flag read cache. Each method returns the cached value for
 * the workspace if present, otherwise invokes the supplied loader, caches its result and returns it.
 * Writes evict the whole bucket (toggles are rare). Implemented by a driven cache adapter.
 */
public interface FeatureFlagCachePort {

    /** Cached effective on/off for one flag in a workspace. */
    boolean isEnabled(UUID workspaceId, String key, BooleanSupplier loader);

    /** Cached effective state of every flag in a workspace, keyed by name. */
    Map<String, Boolean> effectiveMap(UUID workspaceId, Supplier<Map<String, Boolean>> loader);

    /** Cached admin view of the flags for a workspace. */
    List<FeatureFlagState> list(UUID workspaceId, Supplier<List<FeatureFlagState>> loader);

    /** Evict every cached entry — invoked after any write. */
    void evictAll();
}
