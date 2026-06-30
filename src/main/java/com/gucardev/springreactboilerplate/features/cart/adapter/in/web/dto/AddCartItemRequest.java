package com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Add a product to a cart. Re-adding a SKU already in the cart merges into the existing line.
 *
 * <p>The Bean Validation here is a cheap first gate on transport shape; the real business rules
 * (SKU format, per-line/per-cart limits, merge behaviour) are enforced by the {@code Cart} aggregate.
 */
@Schema(description = "Add a product to a cart (re-adding a SKU merges into the existing line).")
public record AddCartItemRequest(

        @Schema(description = "Stock-keeping unit (A–Z, 0–9, dashes; case-insensitive)", example = "KBD-01")
        @NotBlank
        @Size(max = 32)
        String sku,

        @Schema(description = "Product name", example = "Mechanical keyboard")
        @NotBlank
        @Size(max = 150)
        String productName,

        @Schema(description = "Unit price", example = "129.90")
        @NotNull
        @DecimalMin("0.00")
        @Digits(integer = 10, fraction = 2)
        BigDecimal unitPrice,

        @Schema(description = "Quantity to add", example = "2")
        @NotNull
        @Min(1)
        Integer quantity
) {
}
