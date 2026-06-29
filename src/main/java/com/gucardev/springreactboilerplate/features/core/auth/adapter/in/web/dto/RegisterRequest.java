package com.gucardev.springreactboilerplate.features.core.auth.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Self-registration payload.")
public record RegisterRequest(

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
        String phoneNumber
) {
}
