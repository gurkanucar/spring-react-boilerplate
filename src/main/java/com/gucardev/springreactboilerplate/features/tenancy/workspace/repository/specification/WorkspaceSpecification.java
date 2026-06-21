package com.gucardev.springreactboilerplate.features.tenancy.workspace.repository.specification;

import com.gucardev.springreactboilerplate.features.shared.repository.specification.BaseSpecification;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.entity.Workspace;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.request.WorkspaceFilterRequest;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorkspaceSpecification {

    /**
     * @param organizationId the tenant scope to constrain to; {@code null} means no org constraint
     *                       (only a global super-admin should pass null).
     */
    public static Specification<Workspace> build(WorkspaceFilterRequest filter, UUID organizationId) {
        return BaseSpecification.<Workspace>like("name", filter.getName())
                .and(BaseSpecification.equals("isActive", filter.getIsActive()))
                .and(BaseSpecification.equals("organizationId", organizationId))
                .and(BaseSpecification.createdBetween(filter.getStartDate(), filter.getEndDate()));
    }
}
