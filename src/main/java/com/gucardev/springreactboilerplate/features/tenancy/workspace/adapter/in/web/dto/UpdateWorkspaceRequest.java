package com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "Payload for updating a workspace. Null fields are left unchanged.")
public record UpdateWorkspaceRequest(

        @Schema(description = "Display name")
        @Size(max = 200)
        String name,

        @Schema(description = "URL-safe unique slug")
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

        @Schema(description = "Brand accent color (hex)")
        @Size(max = 20)
        String brandColor,

        @Schema(description = "Whether the workspace is active")
        Boolean isActive,

        @Schema(description = "Logo file id")
        UUID logoId
) {
}
