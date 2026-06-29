package com.gucardev.springreactboilerplate.features.tenancy.organization.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link OrganizationJpaEntity}. An implementation detail of the persistence
 * adapter — the application core never sees it, only the organization output ports.
 */
@Repository
public interface OrganizationJpaRepository extends BaseJpaRepository<OrganizationJpaEntity, UUID> {

    boolean existsBySlug(String slug);
}
