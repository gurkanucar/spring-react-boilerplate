package com.gucardev.springreactboilerplate.features.core.auth.application.port.in;

import com.gucardev.springreactboilerplate.features.core.auth.domain.model.AuthTokens;

/**
 * Input port: authenticate email + password and issue a token bundle.
 */
public interface LoginUseCase {

    AuthTokens login(LoginCommand command);
}
