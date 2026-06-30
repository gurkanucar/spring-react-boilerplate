package com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Open a new, empty cart for a customer.")
public record CreateCartRequest(

        @Schema(description = "Customer name", example = "Ada Lovelace")
        @NotBlank
        @Size(max = 150)
        String customerName
) {
}
