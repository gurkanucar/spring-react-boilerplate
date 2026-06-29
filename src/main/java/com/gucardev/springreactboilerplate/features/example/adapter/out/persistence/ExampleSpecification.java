package com.gucardev.springreactboilerplate.features.example.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.example.application.port.in.ExampleSearchCriteria;
import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builds the query {@link Specification} for an {@link ExampleSearchCriteria} by composing the
 * reusable predicates from {@link BaseSpecification}. Each predicate is a no-op when its input is
 * empty, so absent filters simply don't constrain the query.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExampleSpecification {

    public static Specification<ExampleJpaEntity> build(ExampleSearchCriteria criteria) {
        return BaseSpecification.<ExampleJpaEntity>like("name", criteria.name())
                .and(BaseSpecification.createdBetween(criteria.startDate(), criteria.endDate()));
    }
}
