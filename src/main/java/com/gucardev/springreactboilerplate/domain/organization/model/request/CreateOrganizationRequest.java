package com.gucardev.springreactboilerplate.domain.organization.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "Payload for creating an organization.")
public record CreateOrganizationRequest(

        @Schema(description = "Display name", example = "Acme Inc.")
        @NotBlank
        @Size(max = 200)
        String name,

        @Schema(description = "URL-safe unique slug (lowercase letters, digits, hyphens)", example = "acme")
        @NotBlank
        @Size(max = 100)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "{slug.pattern.exception}")
        String slug,

        @Schema(description = "Free-text description")
        @Size(max = 1000)
        String description,

        @Schema(description = "Phone number", example = "+1-555-0100")
        @Size(max = 50)
        String phoneNumber,

        @Schema(description = "Address")
        @Size(max = 500)
        String address,

        @Schema(description = "Whether the organization is active (defaults to true)", example = "true")
        Boolean isActive,

        @Schema(description = "Logo file id")
        UUID logoId
) {
}
