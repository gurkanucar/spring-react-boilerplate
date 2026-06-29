package com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in;

import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;

/**
 * Input port: create an organization (tenant). Driving adapters depend on this interface, not on the
 * implementing service.
 */
public interface CreateOrganizationUseCase {

    Organization create(CreateOrganizationCommand command);
}
