package com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out;

import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port: load organizations and run existence checks against the store. Implemented by a driven
 * persistence adapter.
 */
public interface LoadOrganizationPort {

    Optional<Organization> findById(UUID id);

    boolean existsById(UUID id);

    boolean existsBySlug(String slug);
}
