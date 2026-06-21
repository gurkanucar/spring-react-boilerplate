package com.gucardev.springreactboilerplate.features.tenancy.workspace.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "Payload for creating a workspace in the caller's organization.")
public record CreateWorkspaceRequest(

        @Schema(description = "Display name", example = "Downtown Branch")
        @NotBlank
        @Size(max = 200)
        String name,

        @Schema(description = "URL-safe unique slug", example = "downtown")
        @NotBlank
        @Size(max = 100)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "{slug.pattern.exception}")
        String slug,

        @Schema(description = "Free-text description")
        @Size(max = 1000)
        String description,

        @Schema(description = "Phone number")
        @Size(max = 50)
        String phoneNumber,

        @Schema(description = "Address")
        @Size(max = 500)
        String address,

        @Schema(description = "Brand accent color (hex)", example = "#b8732b")
        @Size(max = 20)
        String brandColor,

        @Schema(description = "Whether the workspace is active (defaults to true)", example = "true")
        Boolean isActive,

        @Schema(description = "Logo file id")
        UUID logoId,

        @Schema(description = "Target organization id (super-admin only; org users always use their own org)")
        UUID organizationId
) {
}
