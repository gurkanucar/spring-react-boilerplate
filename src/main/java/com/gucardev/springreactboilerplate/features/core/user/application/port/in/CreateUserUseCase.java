package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;

/**
 * Input port: create a user (admin). Driving adapters depend on this interface, not the service.
 */
public interface CreateUserUseCase {

    User create(CreateUserCommand command);
}
