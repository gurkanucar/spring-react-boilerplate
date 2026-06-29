package com.gucardev.springreactboilerplate.features.news.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link NewsJpaEntity}. An implementation detail of the persistence
 * adapter — the application core never sees it, only the news output ports.
 */
@Repository
public interface NewsJpaRepository extends BaseJpaRepository<NewsJpaEntity, UUID> {

    boolean existsByWorkspaceIdAndSlug(UUID workspaceId, String slug);
}
