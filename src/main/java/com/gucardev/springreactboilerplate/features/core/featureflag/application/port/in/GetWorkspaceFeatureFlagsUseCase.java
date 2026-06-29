package com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in;

import com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlagState;
import java.util.List;

/**
 * Input port: admin view of the feature flags for the current workspace — the known catalog flags
 * plus any stored custom flags, each with its effective value and whether it is the default.
 */
public interface GetWorkspaceFeatureFlagsUseCase {

    List<FeatureFlagState> getForCurrentWorkspace();
}
