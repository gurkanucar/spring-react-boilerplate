package com.gucardev.springreactboilerplate.features.tenancy.organization.repository;

import com.gucardev.springreactboilerplate.features.tenancy.organization.entity.Organization;
import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends BaseJpaRepository<Organization, UUID> {

    boolean existsBySlug(String slug);
}
