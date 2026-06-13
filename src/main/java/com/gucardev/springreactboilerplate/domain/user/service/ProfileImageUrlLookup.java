package com.gucardev.springreactboilerplate.domain.user.service;

import com.gucardev.springreactboilerplate.domain.file.model.dto.FileUrlDto;
import java.util.UUID;

/**
 * Supplies a profile image's resolved URLs by file id, returning {@code null} when there is none.
 * Passed to {@code UserMapper} as a {@code @Context} so the same enrichment works both for a single
 * user (lazy per-id lookup) and for a page of users (a pre-resolved batch map — no N+1).
 */
@FunctionalInterface
public interface ProfileImageUrlLookup {

    FileUrlDto resolve(UUID profileImageId);
}
