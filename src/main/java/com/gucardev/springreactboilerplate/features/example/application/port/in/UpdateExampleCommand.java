package com.gucardev.springreactboilerplate.features.example.application.port.in;

/**
 * Driving-side command for updating an example. Null fields are left unchanged.
 */
public record UpdateExampleCommand(
        String name,
        String description,
        Boolean active
) {
}
