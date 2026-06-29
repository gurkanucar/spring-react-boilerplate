package com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in;

import java.util.UUID;

/**
 * Input port: delete an organization (tenant) by id.
 */
public interface DeleteOrganizationUseCase {

    void delete(UUID id);
}
