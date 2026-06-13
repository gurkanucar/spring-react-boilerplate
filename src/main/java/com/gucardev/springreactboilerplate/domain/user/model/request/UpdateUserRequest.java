package com.gucardev.springreactboilerplate.domain.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.Set;

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
        Set<String> roles
) {
}
