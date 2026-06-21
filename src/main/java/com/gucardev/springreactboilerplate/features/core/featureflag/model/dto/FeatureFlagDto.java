package com.gucardev.springreactboilerplate.features.core.featureflag.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Effective feature-flag state for the current workspace.")
public record FeatureFlagDto(

        @Schema(description = "Flag key", example = "NEWS_MODULE")
        String key,

        @Schema(description = "Effective on/off value for this workspace", example = "true")
        boolean enabled,

        @Schema(description = "True when no per-workspace override is stored (the catalog default is used)",
                example = "false")
        boolean isDefault
) {
}
