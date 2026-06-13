package com.gucardev.springreactboilerplate.domain.workspace.repository.specification;

import com.gucardev.springreactboilerplate.domain.shared.repository.specification.BaseSpecification;
import com.gucardev.springreactboilerplate.domain.workspace.entity.Workspace;
import com.gucardev.springreactboilerplate.domain.workspace.model.request.WorkspaceFilterRequest;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class WorkspaceSpecification {

    private WorkspaceSpecification() {
    }

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
