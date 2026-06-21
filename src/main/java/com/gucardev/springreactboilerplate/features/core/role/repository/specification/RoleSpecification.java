package com.gucardev.springreactboilerplate.features.core.role.repository.specification;

import com.gucardev.springreactboilerplate.features.core.role.entity.Role;
import com.gucardev.springreactboilerplate.features.core.role.model.request.RoleFilterRequest;
import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builds the query {@link Specification} for a {@link RoleFilterRequest} by composing reusable
 * predicates from {@link BaseSpecification}. Each predicate is a no-op when its input is empty.
 */
public final class RoleSpecification {

    private RoleSpecification() {
    }

    public static Specification<Role> build(RoleFilterRequest filter) {
        return BaseSpecification.<Role>like("name", filter.getName())
                .and(BaseSpecification.createdBetween(filter.getStartDate(), filter.getEndDate()));
    }
}
