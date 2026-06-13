package com.gucardev.springreactboilerplate.domain.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

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
        Set<String> roles
) {
}
