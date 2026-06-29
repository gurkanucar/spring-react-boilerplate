package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in;

import java.util.UUID;

/**
 * Driving-side command for updating a workspace. Any {@code null} field is left unchanged on the
 * target workspace; the owning organization is never reassigned through an update.
 */
public record UpdateWorkspaceCommand(
        String name,
        String slug,
        String description,
        String phoneNumber,
        String address,
        String brandColor,
        Boolean isActive,
        UUID logoId
) {
}
