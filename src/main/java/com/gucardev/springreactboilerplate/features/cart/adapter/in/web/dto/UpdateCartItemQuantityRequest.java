package com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Set a line's quantity to an exact value. A quantity of 0 removes the line.")
public record UpdateCartItemQuantityRequest(

        @Schema(description = "New quantity for the line (0 removes it)", example = "3")
        @NotNull
        @Min(0)
        Integer quantity
) {
}
