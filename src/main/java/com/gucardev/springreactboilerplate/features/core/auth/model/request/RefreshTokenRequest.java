package com.gucardev.springreactboilerplate.features.core.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Exchange a refresh token for a fresh access token (and a rotated refresh token).")
public record RefreshTokenRequest(

        @Schema(description = "A previously issued refresh token")
        @NotBlank
        @Size(max = 4096)
        String refreshToken
) {
}
