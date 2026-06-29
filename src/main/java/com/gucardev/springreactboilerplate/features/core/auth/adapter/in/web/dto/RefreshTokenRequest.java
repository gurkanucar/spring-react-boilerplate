package com.gucardev.springreactboilerplate.features.core.auth.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Exchange a refresh token for a fresh access token (and a rotated refresh token).")
public record RefreshTokenRequest(

        @Schema(description = "A previously issued refresh token")
        @NotBlank
        String refreshToken
) {
}
