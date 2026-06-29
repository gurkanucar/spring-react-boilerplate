package com.gucardev.springreactboilerplate.features.core.featureflag.domain.model;

import lombok.Builder;

/**
 * Pure domain read model describing the effective state of a single feature flag for a workspace: its
 * key, the on/off value in force, and whether that value is the catalog default (no stored override).
 *
 * <p>Returned by the read-side input ports; a web mapper turns it into the response DTO at the edge.
 */
@Builder
public record FeatureFlagState(
        String key,
        Boolean enabled,
        Boolean isDefault
) {
}
