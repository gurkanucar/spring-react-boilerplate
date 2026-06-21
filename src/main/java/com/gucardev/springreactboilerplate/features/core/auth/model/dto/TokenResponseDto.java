package com.gucardev.springreactboilerplate.features.core.auth.model.dto;

import com.gucardev.springreactboilerplate.features.core.user.model.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Issued credentials: a bearer access token plus a rotating refresh token.")
public record TokenResponseDto(

        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,

        @Schema(description = "Opaque refresh token", example = "0f8c2b1a-...")
        String refreshToken,

        @Schema(description = "Token scheme to use in the Authorization header", example = "Bearer")
        String tokenType,

        @Schema(description = "Access-token lifetime in seconds", example = "48000")
        Long expiresIn,

        @Schema(description = "The authenticated user")
        UserResponseDto user
) {
}
