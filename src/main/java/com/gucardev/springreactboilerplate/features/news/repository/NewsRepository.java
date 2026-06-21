package com.gucardev.springreactboilerplate.features.news.repository;

import com.gucardev.springreactboilerplate.features.news.entity.News;
import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends BaseJpaRepository<News, UUID> {

    boolean existsByWorkspaceIdAndSlug(UUID workspaceId, String slug);
}
