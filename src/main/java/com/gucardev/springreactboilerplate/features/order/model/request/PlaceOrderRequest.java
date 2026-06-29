package com.gucardev.springreactboilerplate.features.order.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "Place an order; persists the order and an OrderCreated outbox event atomically.")
public record PlaceOrderRequest(

        @Schema(description = "Customer name", example = "Ada Lovelace")
        @NotBlank
        @Size(max = 150)
        String customerName,

        @Schema(description = "Product ordered", example = "Mechanical keyboard")
        @NotBlank
        @Size(max = 150)
        String product,

        @Schema(description = "Quantity ordered", example = "2")
        @NotNull
        @Min(1)
        Integer quantity,

        @Schema(description = "Order amount", example = "129.90")
        @NotNull
        @DecimalMin("0.00")
        @Digits(integer = 10, fraction = 2)
        BigDecimal amount
) {
}
