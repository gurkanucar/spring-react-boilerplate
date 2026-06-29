package com.gucardev.springreactboilerplate.features.example.application.port.in;

/**
 * Driving-side command for creating an example. Carries already-validated input from a driving
 * adapter into the application core.
 */
public record CreateExampleCommand(
        String name,
        String description,
        Boolean active
) {
}
