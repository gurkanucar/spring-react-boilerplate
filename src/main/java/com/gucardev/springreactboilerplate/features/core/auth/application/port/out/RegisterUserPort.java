package com.gucardev.springreactboilerplate.features.core.auth.application.port.out;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;

/**
 * Output port: create a self-registered account. Backed by an adapter delegating to the user
 * feature's registration input port, keeping the auth core off the user internals.
 */
public interface RegisterUserPort {

    User register(RegisterUserData data);

    /** Data passed to the user feature to create a self-registered account. */
    record RegisterUserData(
            String email,
            String password,
            String name,
            String surname,
            String phoneNumber
    ) {
    }
}
