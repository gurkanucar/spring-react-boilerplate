package com.gucardev.springreactboilerplate.features.cart.application.port.in;

/**
 * Driving-side command for opening a cart. Carries input from a driving adapter into the application
 * core, decoupling the core from any particular transport.
 */
public record CreateCartCommand(String customerName) {
}
