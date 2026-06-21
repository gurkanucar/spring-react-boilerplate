package com.gucardev.springreactboilerplate.features.tenancy.workspace.repository;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.entity.Workspace;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends BaseJpaRepository<Workspace, UUID> {

    boolean existsBySlug(String slug);
}
