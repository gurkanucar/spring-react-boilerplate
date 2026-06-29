package com.gucardev.springreactboilerplate.features.core.notification.adapter.out.featureflag;

import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in.IsFeatureEnabledUseCase;
import com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlags;
import com.gucardev.springreactboilerplate.features.core.notification.application.port.out.FeatureFlagCheckPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing {@link FeatureFlagCheckPort} by delegating to the featureflag feature. This is
 * the ONLY place in the notification module that depends on featureflag types, keeping the application
 * and domain layers decoupled from that feature.
 */
@Component
@RequiredArgsConstructor
public class FeatureFlagCheckAdapter implements FeatureFlagCheckPort {

    private final IsFeatureEnabledUseCase isFeatureEnabledUseCase;

    @Override
    public boolean isInAppNotificationsEnabled(UUID workspaceId) {
        return isFeatureEnabledUseCase.isEnabled(workspaceId, FeatureFlags.IN_APP_NOTIFICATIONS);
    }
}
