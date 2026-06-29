package com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out;

import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;

/**
 * Output port: delete an organization from the store.
 */
public interface DeleteOrganizationPort {

    void delete(Organization organization);
}
