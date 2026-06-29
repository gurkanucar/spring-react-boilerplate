package com.gucardev.springreactboilerplate.features.core.auth.application.service;

import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.RegisterCommand;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.RegisterUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.RegisterUserPort;
import com.gucardev.springreactboilerplate.features.core.auth.domain.model.AuthTokens;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registers a new account via the user feature (default {@code USER} role, hashed password) and
 * immediately issues tokens (auto-login).
 */
@Service
@RequiredArgsConstructor
public class RegisterService implements RegisterUseCase {

    private final RegisterUserPort registerUserPort;
    private final AuthTokenService authTokenService;

    @Override
    @Transactional
    public AuthTokens register(RegisterCommand command) {
        User user = registerUserPort.register(new RegisterUserPort.RegisterUserData(
                command.email(), command.password(), command.name(),
                command.surname(), command.phoneNumber()));
        return authTokenService.issueTokens(user);
    }
}
