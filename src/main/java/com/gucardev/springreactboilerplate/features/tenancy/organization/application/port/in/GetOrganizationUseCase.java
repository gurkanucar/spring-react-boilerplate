package com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in;

import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;
import java.util.UUID;

/**
 * Input port: read a single organization (tenant). This is the public read API other features
 * (workspace, user) depend on — {@link #getById(UUID)} fetches-or-404s, while {@link #existsById(UUID)}
 * lets callers run their own existence checks without coupling to the persistence layer.
 */
public interface GetOrganizationUseCase {

    Organization getById(UUID id);

    boolean existsById(UUID id);
}
