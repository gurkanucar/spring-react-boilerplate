package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.user.application.exception.UserExceptionType;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.OrganizationLookupPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.WorkspaceLookupPort;
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

    private final OrganizationLookupPort organizationLookupPort;
    private final WorkspaceLookupPort workspaceLookupPort;

    public void validate(UUID organizationId, UUID workspaceId) {
        if (organizationId != null && !organizationLookupPort.existsById(organizationId)) {
            throw UserExceptionType.ORGANIZATION_NOT_FOUND.toException();
        }
        if (workspaceId == null) {
            return;
        }
        if (organizationId == null) {
            throw UserExceptionType.WORKSPACE_REQUIRES_ORGANIZATION.toException();
        }
        UUID workspaceOrganizationId = workspaceLookupPort.findOrganizationId(workspaceId).orElse(null);
        if (!organizationId.equals(workspaceOrganizationId)) {
            throw UserExceptionType.WORKSPACE_NOT_IN_ORGANIZATION.toException();
        }
    }
}
