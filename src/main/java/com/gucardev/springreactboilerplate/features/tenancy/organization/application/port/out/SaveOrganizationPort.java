package com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out;

import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;

/**
 * Output port: persist an organization (insert or update) and return the stored state, including any
 * generated id and audit metadata.
 */
public interface SaveOrganizationPort {

    Organization save(Organization organization);
}
