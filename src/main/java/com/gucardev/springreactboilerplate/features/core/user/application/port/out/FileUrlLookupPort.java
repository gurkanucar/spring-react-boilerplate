package com.gucardev.springreactboilerplate.features.core.user.application.port.out;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Output port: resolve stored-file ids into access URLs for profile-image enrichment. Backed by a
 * driven adapter that delegates to the file feature, keeping the user core off the file internals.
 */
public interface FileUrlLookupPort {

    /** Resolves a single file id; returns {@code null} when there is no such file. */
    ProfileImageUrls resolve(UUID fileId);

    /** Batch-resolves several file ids in one shot (used by list endpoints to avoid N+1). */
    Map<UUID, ProfileImageUrls> resolveAll(Set<UUID> fileIds);
}
