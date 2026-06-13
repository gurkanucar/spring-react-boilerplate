package com.gucardev.springreactboilerplate.domain.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Revoke a refresh token.")
public record LogoutRequest(

        @Schema(description = "The refresh token to revoke")
        @NotBlank
        String refreshToken
) {
}
