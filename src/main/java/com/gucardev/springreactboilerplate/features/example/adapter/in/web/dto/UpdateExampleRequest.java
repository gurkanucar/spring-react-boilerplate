package com.gucardev.springreactboilerplate.features.example.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for updating an example. Null fields are left unchanged.")
public record UpdateExampleRequest(

        @Schema(description = "Display name", example = "Updated name")
        @Size(max = 150)
        String name,

        @Schema(description = "Free-text description", example = "Updated description")
        @Size(max = 500)
        String description,

        @Schema(description = "Whether the example is active", example = "false")
        Boolean active
) {
}
