package com.gucardev.springreactboilerplate.features.example.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for creating an example.")
public record CreateExampleRequest(

        @Schema(description = "Display name", example = "Demo example")
        @NotBlank
        @Size(max = 150)
        String name,

        @Schema(description = "Free-text description", example = "Anything you like")
        @Size(max = 500)
        String description,

        @Schema(description = "Whether the example is active", example = "true")
        Boolean active
) {
}
