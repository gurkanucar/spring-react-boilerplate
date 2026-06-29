package com.gucardev.springreactboilerplate.features.core.auth.adapter.out.user;

import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.RegisterUserPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.RegisterUserCommand;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.RegisterUserUseCase;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the auth {@link RegisterUserPort} by delegating to the user feature's
 * registration input port. Confines the auth module's dependency on user types.
 */
@Component
@RequiredArgsConstructor
public class RegisterUserAdapter implements RegisterUserPort {

    private final RegisterUserUseCase registerUserUseCase;

    @Override
    public User register(RegisterUserData data) {
        return registerUserUseCase.register(new RegisterUserCommand(
                data.email(), data.password(), data.name(), data.surname(), data.phoneNumber()));
    }
}
