package com.gucardev.springreactboilerplate.features.core.featureflag.application.port.out;

import com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlag;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port: load feature-flag overrides from the store. Implemented by a driven persistence adapter.
 */
public interface LoadFeatureFlagPort {

    List<FeatureFlag> findByWorkspaceId(UUID workspaceId);

    Optional<FeatureFlag> findByWorkspaceIdAndFlagKey(UUID workspaceId, String flagKey);
}
