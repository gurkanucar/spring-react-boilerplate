package com.gucardev.springreactboilerplate.domain.organization.repository;

import com.gucardev.springreactboilerplate.domain.organization.entity.Organization;
import com.gucardev.springreactboilerplate.domain.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends BaseJpaRepository<Organization, UUID> {

    boolean existsBySlug(String slug);
}
