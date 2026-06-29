package com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in;

import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;
import org.springframework.data.domain.Page;

/**
 * Input port: list organizations (tenants), paged, sorted and filtered.
 */
public interface SearchOrganizationsUseCase {

    Page<Organization> search(OrganizationSearchQuery query);
}
