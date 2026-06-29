package com.gucardev.springreactboilerplate.features.core.role.application.port.in;

/**
 * Driving-side command for creating a role. Carries already-validated input from a driving adapter
 * into the application core, decoupling the core from any particular transport.
 */
public record CreateRoleCommand(
        String name,
        String displayName,
        String description
) {
}
