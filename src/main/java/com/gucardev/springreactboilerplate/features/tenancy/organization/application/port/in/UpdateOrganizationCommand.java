package com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in;

import java.util.UUID;

/**
 * Driving-side command for updating an organization. Null fields are left unchanged.
 */
public record UpdateOrganizationCommand(
        String name,
        String slug,
        String description,
        String phoneNumber,
        String address,
        Boolean isActive,
        UUID logoId
) {
}
