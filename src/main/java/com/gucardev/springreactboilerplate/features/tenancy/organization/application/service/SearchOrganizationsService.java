package com.gucardev.springreactboilerplate.features.tenancy.organization.application.service;

import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.OrganizationSearchQuery;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.SearchOrganizationsUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.OrganizationSearchCriteria;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.SearchOrganizationsPort;
import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchOrganizationsService implements SearchOrganizationsUseCase {

    private final SearchOrganizationsPort searchOrganizationsPort;

    @Override
    @Transactional(readOnly = true)
    public Page<Organization> search(OrganizationSearchQuery query) {
        OrganizationSearchCriteria criteria = new OrganizationSearchCriteria(
                query.name(), query.isActive(), query.startDate(), query.endDate());
        return searchOrganizationsPort.search(criteria, query.pageable());
    }
}
