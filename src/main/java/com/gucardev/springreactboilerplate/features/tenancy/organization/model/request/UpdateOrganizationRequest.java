package com.gucardev.springreactboilerplate.features.tenancy.organization.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "Payload for updating an organization. Null fields are left unchanged.")
public record UpdateOrganizationRequest(

        @Schema(description = "Display name", example = "Acme Inc.")
        @Size(max = 200)
        String name,

        @Schema(description = "URL-safe unique slug", example = "acme")
        @Size(max = 100)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "{slug.pattern.exception}")
        String slug,

        @Schema(description = "Free-text description")
        @Size(max = 1000)
        String description,

        @Schema(description = "Phone number")
        @Size(max = 50)
        @Pattern(regexp = "^\\+?[0-9 ()\\-]{5,}$", message = "{phone.pattern.exception}")
        String phoneNumber,

        @Schema(description = "Address")
        @Size(max = 500)
        String address,

        @Schema(description = "Whether the organization is active")
        Boolean isActive,

        @Schema(description = "Logo file id")
        UUID logoId
) {
}
