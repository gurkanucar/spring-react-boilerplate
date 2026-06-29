package com.gucardev.springreactboilerplate.features.core.auth.application.port.in;

/**
 * Driving-side command carrying self-registration input into the application core.
 */
public record RegisterCommand(
        String email,
        String password,
        String name,
        String surname,
        String phoneNumber
) {
}
