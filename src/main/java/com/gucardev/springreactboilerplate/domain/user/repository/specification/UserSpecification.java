package com.gucardev.springreactboilerplate.domain.user.repository.specification;

import com.gucardev.springreactboilerplate.domain.shared.repository.specification.BaseSpecification;
import com.gucardev.springreactboilerplate.domain.user.entity.User;
import com.gucardev.springreactboilerplate.domain.user.model.request.UserFilterRequest;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builds the query {@link Specification} for a {@link UserFilterRequest} by composing reusable
 * predicates from {@link BaseSpecification}. Each predicate is a no-op when its input is empty.
 */
public final class UserSpecification {

    private UserSpecification() {
    }

    public static Specification<User> build(UserFilterRequest filter) {
        return BaseSpecification.<User>like("email", filter.getEmail())
                .and(BaseSpecification.like("name", filter.getName()))
                .and(BaseSpecification.equals("activated", filter.getActivated()))
                .and(BaseSpecification.equals("isActive", filter.getIsActive()))
                .and(BaseSpecification.createdBetween(filter.getStartDate(), filter.getEndDate()));
    }
}
