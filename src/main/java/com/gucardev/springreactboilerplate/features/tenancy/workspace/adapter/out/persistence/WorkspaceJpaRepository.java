package com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link WorkspaceJpaEntity}. An implementation detail of the persistence
 * adapter — the application core never sees it, only the workspace output ports.
 */
@Repository
public interface WorkspaceJpaRepository extends BaseJpaRepository<WorkspaceJpaEntity, UUID> {

    boolean existsBySlug(String slug);
}
