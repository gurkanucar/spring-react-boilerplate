package com.gucardev.springreactboilerplate.features.core.role.application.port.in;

/**
 * Driving-side command for updating a role. Null fields are left unchanged. The {@code name} is
 * immutable (it is the key Spring Security authorities are derived from), so only the descriptive
 * fields can be edited.
 */
public record UpdateRoleCommand(
        String displayName,
        String description
) {
}
