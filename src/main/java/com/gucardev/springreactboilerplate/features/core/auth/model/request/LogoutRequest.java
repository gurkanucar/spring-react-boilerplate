package com.gucardev.springreactboilerplate.features.core.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Revoke a refresh token.")
public record LogoutRequest(

        @Schema(description = "The refresh token to revoke")
        @NotBlank
        @Size(max = 4096)
        String refreshToken
) {
}
