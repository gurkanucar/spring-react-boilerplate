package com.gucardev.springreactboilerplate.features.core.user.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

/**
 * Payload for an admin updating a user. Null fields are left unchanged. Email and password are
 * not editable here (identity / dedicated password flow); pass {@code roles} to replace the
 * user's role set.
 */
@Schema(description = "Payload for updating a user (admin). Null fields are left unchanged.")
public record UpdateUserRequest(

        @Schema(description = "First name", example = "Jane")
        @Size(max = 100)
        String name,

        @Schema(description = "Last name", example = "Doe")
        @Size(max = 100)
        String surname,

        @Schema(description = "Phone number", example = "+1-555-0100")
        @Size(max = 30)
        String phoneNumber,

        @Schema(description = "Whether the email is verified", example = "true")
        Boolean activated,

        @Schema(description = "Whether the account is enabled", example = "false")
        Boolean isActive,

        @Schema(description = "Replacement role names (no ROLE_ prefix)", example = "[\"ADMIN\",\"USER\"]")
        Set<String> roles,

        @Schema(description = "Reassign the user to this organization; null leaves it unchanged",
                example = "7a2b1c9d-3e4f-5a6b-7c8d-9e0f1a2b3c4d")
        UUID organizationId,

        @Schema(description = "Pin the user to this workspace; null leaves it unchanged")
        UUID workspaceId
) {
}
