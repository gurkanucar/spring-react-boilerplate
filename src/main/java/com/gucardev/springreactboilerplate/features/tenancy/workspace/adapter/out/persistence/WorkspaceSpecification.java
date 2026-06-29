package com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.WorkspaceSearchCriteria;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builds the JPA {@link Specification} for workspace searches from the application-level
 * {@link WorkspaceSearchCriteria}. Lives in the persistence adapter because it is coupled to the JPA
 * entity field names.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorkspaceSpecification {

    public static Specification<WorkspaceJpaEntity> build(WorkspaceSearchCriteria criteria) {
        return BaseSpecification.<WorkspaceJpaEntity>like("name", criteria.name())
                .and(BaseSpecification.equals("isActive", criteria.isActive()))
                .and(BaseSpecification.equals("organizationId", criteria.organizationId()))
                .and(BaseSpecification.createdBetween(criteria.startDate(), criteria.endDate()));
    }
}
