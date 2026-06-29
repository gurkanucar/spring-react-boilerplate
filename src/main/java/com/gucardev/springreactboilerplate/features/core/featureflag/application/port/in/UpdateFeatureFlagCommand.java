package com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in;

/**
 * Driving-side command to enable or disable a feature flag for the current workspace. Carries
 * already-validated input from a driving adapter into the application core.
 */
public record UpdateFeatureFlagCommand(
        String key,
        Boolean enabled
) {
}
