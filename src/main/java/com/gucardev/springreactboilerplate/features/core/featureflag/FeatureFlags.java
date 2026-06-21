package com.gucardev.springreactboilerplate.features.core.featureflag;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Catalog of the feature flags the product ships with. These are the keys shown in the admin toggle
 * UI and enabled for every new workspace by the seeder. Add a new flag by declaring a constant here
 * and appending it to {@link #KNOWN}. Stored overrides may also use ad-hoc keys not listed here.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureFlags {

    /** Workspace-scoped News module (create/list/publish news entries). */
    public static final String NEWS_MODULE = "NEWS_MODULE";

    /** Allow file attachments on content (e.g. documents on news entries). */
    public static final String FILE_ATTACHMENTS = "FILE_ATTACHMENTS";

    /** Send transactional email notifications for workspace events. */
    public static final String EMAIL_NOTIFICATIONS = "EMAIL_NOTIFICATIONS";

    /** Analytics dashboard for the workspace. */
    public static final String ADVANCED_ANALYTICS = "ADVANCED_ANALYTICS";

    /** Record an audit trail of changes within the workspace. */
    public static final String AUDIT_LOG = "AUDIT_LOG";

    /** Catalog rendered in the admin toggle UI and used by the seeder. Add new flags here. */
    public static final List<String> KNOWN =
            List.of(NEWS_MODULE, FILE_ATTACHMENTS, EMAIL_NOTIFICATIONS, ADVANCED_ANALYTICS, AUDIT_LOG);
}
