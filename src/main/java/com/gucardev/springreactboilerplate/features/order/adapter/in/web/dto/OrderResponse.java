package com.gucardev.springreactboilerplate.features.order.adapter.in.web.dto;

import com.gucardev.springreactboilerplate.features.order.domain.model.OrderStatus;
import com.gucardev.springreactboilerplate.features.shared.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "An order with its lifecycle status and audit metadata.")
public class OrderResponse extends BaseDto {

    @Schema(description = "Order id (also the aggregate id carried in the OrderCreated event)")
    private UUID id;

    @Schema(description = "Customer name", example = "Ada Lovelace")
    private String customerName;

    @Schema(description = "Product ordered", example = "Mechanical keyboard")
    private String product;

    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Order amount", example = "129.90")
    private BigDecimal amount;

    @Schema(description = "Lifecycle status; flips to CONFIRMED once the consumer handles the event",
            example = "PLACED")
    private OrderStatus status;
}
