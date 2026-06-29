package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import java.util.Set;
import java.util.UUID;

/**
 * Driving-side command for updating a user (admin). Null fields are left unchanged; pass
 * {@code roles} to replace the user's role set.
 */
public record UpdateUserCommand(
        String name,
        String surname,
        String phoneNumber,
        Boolean activated,
        Boolean isActive,
        Set<String> roles,
        UUID organizationId,
        UUID workspaceId
) {
}
