package com.gucardev.springreactboilerplate.features.core.featureflag.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload to enable or disable a feature flag for the current workspace.")
public record UpdateFeatureFlagRequest(

        @Schema(description = "Whether the flag should be enabled", example = "true")
        @NotNull
        Boolean enabled
) {
}
