package com.gucardev.springreactboilerplate.features.core.user.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.user.application.port.out.UserSearchCriteria;
import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builds the query {@link Specification} for a {@link UserSearchCriteria} by composing reusable
 * predicates from {@link BaseSpecification}. Each predicate is a no-op when its input is empty.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserSpecification {

    public static Specification<UserJpaEntity> build(UserSearchCriteria criteria) {
        return BaseSpecification.<UserJpaEntity>like("email", criteria.email())
                .and(BaseSpecification.like("name", criteria.name()))
                .and(BaseSpecification.equals("activated", criteria.activated()))
                .and(BaseSpecification.equals("isActive", criteria.isActive()))
                .and(BaseSpecification.createdBetween(criteria.startDate(), criteria.endDate()));
    }
}
