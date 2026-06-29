package com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.DeleteWorkspacePort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.LoadWorkspacePort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.SaveWorkspacePort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.SearchWorkspacePort;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.WorkspaceSearchCriteria;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the workspace load/save/delete/search output ports with Spring Data JPA.
 * Maps domain ⇄ entity at the boundary so the application core stays persistence-agnostic.
 */
@Component
@RequiredArgsConstructor
public class WorkspacePersistenceAdapter
        implements LoadWorkspacePort, SaveWorkspacePort, DeleteWorkspacePort, SearchWorkspacePort {

    private final WorkspaceJpaRepository repository;
    private final WorkspacePersistenceMapper mapper;

    @Override
    public Optional<Workspace> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }

    @Override
    public Workspace save(Workspace workspace) {
        return mapper.toDomain(repository.save(mapper.toEntity(workspace)));
    }

    @Override
    public void delete(Workspace workspace) {
        repository.delete(mapper.toEntity(workspace));
    }

    @Override
    public Page<Workspace> search(WorkspaceSearchCriteria criteria) {
        return repository.findAll(WorkspaceSpecification.build(criteria), criteria.pageable())
                .map(mapper::toDomain);
    }
}
