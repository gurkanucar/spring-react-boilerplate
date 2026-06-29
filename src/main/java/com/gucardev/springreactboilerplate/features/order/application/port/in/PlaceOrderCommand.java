package com.gucardev.springreactboilerplate.features.order.application.port.in;

import java.math.BigDecimal;

/**
 * Driving-side command for placing an order. Carries already-validated input from a driving adapter
 * into the application core, decoupling the core from any particular transport (web request, CLI, ...).
 */
public record PlaceOrderCommand(
        String customerName,
        String product,
        Integer quantity,
        BigDecimal amount
) {
}
