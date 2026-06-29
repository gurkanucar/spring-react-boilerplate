package com.gucardev.springreactboilerplate.features.core.auth.application.service;

import com.gucardev.springreactboilerplate.features.core.auth.application.exception.AuthExceptionType;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.LoginCommand;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.LoginUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.AuthenticatePort;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.LoadUserPort;
import com.gucardev.springreactboilerplate.features.core.auth.domain.model.AuthTokens;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticates email + password through the authentication port (which raises
 * {@code BadCredentialsException}/{@code DisabledException}, both translated centrally by
 * {@code GlobalExceptionHandler}) and then issues a token bundle.
 */
@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final AuthenticatePort authenticatePort;
    private final LoadUserPort loadUserPort;
    private final AuthTokenService authTokenService;

    @Override
    @Transactional
    public AuthTokens login(LoginCommand command) {
        authenticatePort.authenticate(command.email(), command.password());

        User user = loadUserPort.findByEmail(command.email())
                .orElseThrow(AuthExceptionType.USER_NOT_FOUND::toException);

        return authTokenService.issueTokens(user);
    }
}
