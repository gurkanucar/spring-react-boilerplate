package com.gucardev.springreactboilerplate.domain.user.service.usecase;

import com.gucardev.springreactboilerplate.domain.organization.repository.OrganizationRepository;
import com.gucardev.springreactboilerplate.domain.user.exception.UserExceptionType;
import com.gucardev.springreactboilerplate.domain.workspace.entity.Workspace;
import com.gucardev.springreactboilerplate.domain.workspace.repository.WorkspaceRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Validates a user's tenant assignment: the organization must exist, a workspace pin requires an
 * organization, and that workspace must belong to it. Keeps a user from being attached to a missing
 * organization or to a workspace outside their org.
 */
@Service
@RequiredArgsConstructor
public class UserTenantAssignmentValidator {

    private final WorkspaceRepository workspaceRepository;
    private final OrganizationRepository organizationRepository;

    public void validate(UUID organizationId, UUID workspaceId) {
        if (organizationId != null && !organizationRepository.existsById(organizationId)) {
            throw UserExceptionType.ORGANIZATION_NOT_FOUND.toException();
        }
        if (workspaceId == null) {
            return;
        }
        if (organizationId == null) {
            throw UserExceptionType.WORKSPACE_REQUIRES_ORGANIZATION.toException();
        }
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);
        if (workspace == null || !organizationId.equals(workspace.getOrganizationId())) {
            throw UserExceptionType.WORKSPACE_NOT_IN_ORGANIZATION.toException();
        }
    }
}
