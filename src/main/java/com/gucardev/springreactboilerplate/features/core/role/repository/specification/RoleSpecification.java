package com.gucardev.springreactboilerplate.features.core.role.repository.specification;

import com.gucardev.springreactboilerplate.features.core.role.entity.Role;
import com.gucardev.springreactboilerplate.features.core.role.model.request.RoleFilterRequest;
import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builds the query {@link Specification} for a {@link RoleFilterRequest} by composing reusable
 * predicates from {@link BaseSpecification}. Each predicate is a no-op when its input is empty.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoleSpecification {

    public static Specification<Role> build(RoleFilterRequest filter) {
        return BaseSpecification.<Role>like("name", filter.getName())
                .and(BaseSpecification.createdBetween(filter.getStartDate(), filter.getEndDate()));
    }
}
