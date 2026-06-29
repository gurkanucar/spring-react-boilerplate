package com.gucardev.springreactboilerplate.features.core.featureflag.application.port.out;

import com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlag;

/**
 * Output port: persist a feature-flag override (insert or update) and return the stored state,
 * including any generated id and audit metadata.
 */
public interface SaveFeatureFlagPort {

    FeatureFlag save(FeatureFlag featureFlag);
}
