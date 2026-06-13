package com.gucardev.springreactboilerplate.domain.role.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Payload for updating a role. Null fields are left unchanged. The {@code name} is immutable
 * (it is the key Spring Security authorities are derived from), so only the descriptive fields
 * can be edited.
 */
@Schema(description = "Payload for updating a role. Null fields are left unchanged.")
public record UpdateRoleRequest(

        @Schema(description = "Human-readable label", example = "Senior Manager")
        @Size(max = 100)
        String displayName,

        @Schema(description = "What the role grants", example = "Updated description")
        @Size(max = 255)
        String description
) {
}
