package com.gucardev.springreactboilerplate.features.tenancy.organization.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.OrganizationSearchCriteria;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrganizationSpecification {

    public static Specification<OrganizationJpaEntity> build(OrganizationSearchCriteria criteria) {
        return BaseSpecification.<OrganizationJpaEntity>like("name", criteria.name())
                .and(BaseSpecification.equals("isActive", criteria.isActive()))
                .and(BaseSpecification.createdBetween(criteria.startDate(), criteria.endDate()));
    }
}
