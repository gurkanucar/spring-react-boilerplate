package com.gucardev.springreactboilerplate.features.news.application.port.out;

import com.gucardev.springreactboilerplate.features.news.domain.model.News;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port: load a news entry from the store. Implemented by a driven persistence adapter.
 */
public interface LoadNewsPort {

    Optional<News> findById(UUID id);

    boolean existsByWorkspaceIdAndSlug(UUID workspaceId, String slug);
}
