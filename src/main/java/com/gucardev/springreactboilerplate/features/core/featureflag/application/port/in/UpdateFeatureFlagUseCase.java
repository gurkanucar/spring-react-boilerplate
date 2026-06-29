package com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in;

import com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlagState;

/**
 * Input port: upsert the per-workspace override for a feature flag (any string key) for the current
 * workspace, returning its new effective state.
 */
public interface UpdateFeatureFlagUseCase {

    FeatureFlagState update(UpdateFeatureFlagCommand command);
}
