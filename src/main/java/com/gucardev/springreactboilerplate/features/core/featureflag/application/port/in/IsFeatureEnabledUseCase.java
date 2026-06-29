package com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in;

import java.util.Map;
import java.util.UUID;

/**
 * Input port other features depend on to <em>check</em> feature flags. Consumers (e.g. the
 * notification feature) depend on this interface, never on the implementing service.
 *
 * <p>The effective value of a flag is its stored per-workspace override, or {@code false} when no
 * override exists. Reads are cached per workspace by the application core.
 */
public interface IsFeatureEnabledUseCase {

    /** Effective on/off for one flag in a workspace: the stored override, else {@code false}. */
    boolean isEnabled(UUID workspaceId, String key);

    /** Effective state of the known flags + any stored custom flags, keyed by name. */
    Map<String, Boolean> effectiveFlags(UUID workspaceId);
}
