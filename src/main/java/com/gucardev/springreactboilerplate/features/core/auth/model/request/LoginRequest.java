package com.gucardev.springreactboilerplate.features.core.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Login payload.")
public record LoginRequest(

        @Schema(description = "Email address", example = "admin@mail.com")
        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @Schema(description = "Password", example = "pass")
        @NotBlank
        @Size(max = 100)
        String password
) {
}
