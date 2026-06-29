package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import java.util.Set;
import java.util.UUID;

/**
 * Driving-side command for creating a user (admin). {@code roles} are role names (no {@code ROLE_}
 * prefix); when empty/omitted the user gets the default {@code USER} role. {@code activated}/
 * {@code isActive} default to {@code true} when null.
 */
public record CreateUserCommand(
        String email,
        String password,
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
