package com.gucardev.springreactboilerplate.features.core.auth.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login payload.")
public record LoginRequest(

        @Schema(description = "Email address", example = "admin@mail.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "Password", example = "pass")
        @NotBlank
        String password
) {
}
