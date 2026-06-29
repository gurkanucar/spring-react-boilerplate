package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;

/**
 * Input port: self-register a new account (default {@code USER} role, hashed password) and return
 * the created domain user. Used by the auth feature's registration flow.
 */
public interface RegisterUserUseCase {

    User register(RegisterUserCommand command);
}
