package com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out;

import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Output port: search organizations against the store using filter criteria and paging. Implemented by
 * a driven persistence adapter (backed by a JPA Specification).
 */
public interface SearchOrganizationsPort {

    Page<Organization> search(OrganizationSearchCriteria criteria, Pageable pageable);
}
