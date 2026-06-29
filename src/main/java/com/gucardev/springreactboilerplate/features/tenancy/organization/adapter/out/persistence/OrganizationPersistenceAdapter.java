package com.gucardev.springreactboilerplate.features.tenancy.organization.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.DeleteOrganizationPort;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.LoadOrganizationPort;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.OrganizationSearchCriteria;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.SaveOrganizationPort;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.SearchOrganizationsPort;
import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the organization output ports with Spring Data JPA. Maps domain ⇄ entity at
 * the boundary so the application core stays persistence-agnostic.
 */
@Component
@RequiredArgsConstructor
public class OrganizationPersistenceAdapter
        implements LoadOrganizationPort, SaveOrganizationPort, DeleteOrganizationPort, SearchOrganizationsPort {

    private final OrganizationJpaRepository repository;
    private final OrganizationPersistenceMapper mapper;

    @Override
    public Optional<Organization> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }

    @Override
    public Organization save(Organization organization) {
        return mapper.toDomain(repository.save(mapper.toEntity(organization)));
    }

    @Override
    public void delete(Organization organization) {
        repository.delete(mapper.toEntity(organization));
    }

    @Override
    public Page<Organization> search(OrganizationSearchCriteria criteria, Pageable pageable) {
        return repository.findAll(OrganizationSpecification.build(criteria), pageable).map(mapper::toDomain);
    }
}
