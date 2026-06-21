package com.gucardev.springreactboilerplate.features.core.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

/**
 * Payload for an admin creating a user. {@code roles} are role names (no {@code ROLE_} prefix);
 * when omitted the user gets the default {@code USER} role. {@code activated}/{@code isActive}
 * default to {@code true} when omitted.
 */
@Schema(description = "Payload for creating a user (admin).")
public record CreateUserRequest(

        @Schema(description = "Email address (login)", example = "user@mail.com")
        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @Schema(description = "Plain-text password (stored hashed)", example = "secret123")
        @NotBlank
        @Size(min = 6, max = 100)
        String password,

        @Schema(description = "First name", example = "Jane")
        @NotBlank
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

        @Schema(description = "Whether the account is enabled", example = "true")
        Boolean isActive,

        @Schema(description = "Role names to grant (no ROLE_ prefix)", example = "[\"USER\"]")
        Set<String> roles,

        @Schema(description = "Organization to assign the user to; null for a global super-admin",
                example = "7a2b1c9d-3e4f-5a6b-7c8d-9e0f1a2b3c4d")
        UUID organizationId,

        @Schema(description = "Workspace to pin the user to (an employee); null for an org-level user")
        UUID workspaceId
) {
}
