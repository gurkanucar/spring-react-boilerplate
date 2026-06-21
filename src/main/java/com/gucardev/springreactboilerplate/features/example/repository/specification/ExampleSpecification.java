package com.gucardev.springreactboilerplate.features.example.repository.specification;

import com.gucardev.springreactboilerplate.features.example.entity.Example;
import com.gucardev.springreactboilerplate.features.example.model.request.ExampleFilterRequest;
import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builds the query {@link Specification} for an {@link ExampleFilterRequest} by composing
 * the reusable predicates from {@link BaseSpecification}. Each predicate is a no-op when its
 * input is empty, so absent filters simply don't constrain the query.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExampleSpecification {

    public static Specification<Example> build(ExampleFilterRequest filter) {
        return BaseSpecification.<Example>like("name", filter.getName())
                .and(BaseSpecification.createdBetween(filter.getStartDate(), filter.getEndDate()));
    }
}
