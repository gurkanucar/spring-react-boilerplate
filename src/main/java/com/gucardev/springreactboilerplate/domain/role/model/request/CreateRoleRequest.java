package com.gucardev.springreactboilerplate.domain.role.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for creating a role.")
public record CreateRoleRequest(

        @Schema(description = "Role name (no ROLE_ prefix)", example = "MANAGER")
        @NotBlank
        @Size(max = 50)
        String name,

        @Schema(description = "Human-readable label", example = "Manager")
        @Size(max = 100)
        String displayName,

        @Schema(description = "What the role grants", example = "Manages staff and resources")
        @Size(max = 255)
        String description
) {
}
