package com.gucardev.springreactboilerplate.features.tenancy.organization.repository.specification;

import com.gucardev.springreactboilerplate.features.tenancy.organization.entity.Organization;
import com.gucardev.springreactboilerplate.features.tenancy.organization.model.request.OrganizationFilterRequest;
import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;

public final class OrganizationSpecification {

    private OrganizationSpecification() {
    }

    public static Specification<Organization> build(OrganizationFilterRequest filter) {
        return BaseSpecification.<Organization>like("name", filter.getName())
                .and(BaseSpecification.equals("isActive", filter.getIsActive()))
                .and(BaseSpecification.createdBetween(filter.getStartDate(), filter.getEndDate()));
    }
}
