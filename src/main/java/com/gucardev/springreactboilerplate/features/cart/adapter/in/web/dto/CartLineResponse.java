package com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A single line in a cart, with its computed line total.")
public class CartLineResponse {

    @Schema(description = "Stock-keeping unit (normalised to upper-case)", example = "KBD-01")
    private String sku;

    @Schema(description = "Product name", example = "Mechanical keyboard")
    private String productName;

    @Schema(description = "Unit price", example = "129.90")
    private BigDecimal unitPrice;

    @Schema(description = "Quantity on this line", example = "2")
    private Integer quantity;

    @Schema(description = "Unit price × quantity", example = "259.80")
    private BigDecimal lineTotal;
}
