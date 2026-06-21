package com.gucardev.springreactboilerplate.features.tenancy.organization.repository.specification;

import com.gucardev.springreactboilerplate.features.tenancy.organization.entity.Organization;
import com.gucardev.springreactboilerplate.features.tenancy.organization.model.request.OrganizationFilterRequest;
import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrganizationSpecification {

    public static Specification<Organization> build(OrganizationFilterRequest filter) {
        return BaseSpecification.<Organization>like("name", filter.getName())
                .and(BaseSpecification.equals("isActive", filter.getIsActive()))
                .and(BaseSpecification.createdBetween(filter.getStartDate(), filter.getEndDate()));
    }
}
