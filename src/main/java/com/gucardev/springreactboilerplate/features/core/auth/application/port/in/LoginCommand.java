package com.gucardev.springreactboilerplate.features.core.auth.application.port.in;

/**
 * Driving-side command carrying login credentials into the application core.
 */
public record LoginCommand(
        String email,
        String password
) {
}
