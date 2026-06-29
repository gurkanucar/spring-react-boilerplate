package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in;

import java.util.UUID;

/**
 * Driving-side command for creating a workspace. Carries already-validated input from a driving
 * adapter into the application core, decoupling the core from any particular transport.
 *
 * <p>{@code organizationId} is the target org for a super-admin; org users always create in their
 * own org and this value is ignored.
 */
public record CreateWorkspaceCommand(
        String name,
        String slug,
        String description,
        String phoneNumber,
        String address,
        String brandColor,
        Boolean isActive,
        UUID logoId,
        UUID organizationId
) {
}
