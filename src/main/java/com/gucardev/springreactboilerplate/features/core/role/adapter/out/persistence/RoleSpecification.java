package com.gucardev.springreactboilerplate.features.core.role.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.role.application.port.out.RoleSearchCriteria;
import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builds the query {@link Specification} for a {@link RoleSearchCriteria} by composing reusable
 * predicates from {@link BaseSpecification}. Each predicate is a no-op when its input is empty.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoleSpecification {

    public static Specification<RoleJpaEntity> build(RoleSearchCriteria criteria) {
        return BaseSpecification.<RoleJpaEntity>like("name", criteria.name())
                .and(BaseSpecification.createdBetween(criteria.startDate(), criteria.endDate()));
    }
}
