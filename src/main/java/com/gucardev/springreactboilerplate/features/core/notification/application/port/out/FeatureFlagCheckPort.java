package com.gucardev.springreactboilerplate.features.core.notification.application.port.out;

import java.util.UUID;

/**
 * Output port for checking whether in-app notifications are enabled for a workspace. Keeps the
 * application core decoupled from the featureflag feature: a driven adapter delegates to it, so no
 * featureflag types leak into the domain/application layers.
 */
public interface FeatureFlagCheckPort {

    boolean isInAppNotificationsEnabled(UUID workspaceId);
}
