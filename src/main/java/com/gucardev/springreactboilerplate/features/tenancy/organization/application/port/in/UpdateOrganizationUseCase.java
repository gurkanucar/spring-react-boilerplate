package com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in;

import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;
import java.util.UUID;

/**
 * Input port: update an existing organization (tenant). Null command fields are left unchanged.
 */
public interface UpdateOrganizationUseCase {

    Organization update(UUID id, UpdateOrganizationCommand command);
}
