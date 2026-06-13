package com.gucardev.springreactboilerplate.domain.workspace.repository;

import com.gucardev.springreactboilerplate.domain.shared.repository.BaseJpaRepository;
import com.gucardev.springreactboilerplate.domain.workspace.entity.Workspace;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends BaseJpaRepository<Workspace, UUID> {

    boolean existsBySlug(String slug);
}
