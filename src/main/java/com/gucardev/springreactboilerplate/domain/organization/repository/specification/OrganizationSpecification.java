package com.gucardev.springreactboilerplate.domain.organization.repository.specification;

import com.gucardev.springreactboilerplate.domain.organization.entity.Organization;
import com.gucardev.springreactboilerplate.domain.organization.model.request.OrganizationFilterRequest;
import com.gucardev.springreactboilerplate.domain.shared.repository.specification.BaseSpecification;
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
