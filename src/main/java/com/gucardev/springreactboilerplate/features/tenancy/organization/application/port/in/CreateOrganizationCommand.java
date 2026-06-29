package com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in;

import java.util.UUID;

/**
 * Driving-side command for creating an organization. Carries already-validated input from a driving
 * adapter into the application core, decoupling the core from any particular transport.
 */
public record CreateOrganizationCommand(
        String name,
        String slug,
        String description,
        String phoneNumber,
        String address,
        Boolean isActive,
        UUID logoId
) {
}
