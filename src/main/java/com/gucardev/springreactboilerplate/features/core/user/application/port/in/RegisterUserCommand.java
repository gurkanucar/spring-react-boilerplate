package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

/**
 * Driving-side command for self-registration: a minimal account that is granted the default
 * {@code USER} role. Carried by the auth feature into the user core.
 */
public record RegisterUserCommand(
        String email,
        String password,
        String name,
        String surname,
        String phoneNumber
) {
}
