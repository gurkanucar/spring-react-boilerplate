package com.gucardev.springreactboilerplate.features.core.auth.application.port.in;

import com.gucardev.springreactboilerplate.features.core.auth.domain.model.AuthTokens;

/**
 * Input port: register a new account and immediately issue a token bundle (auto-login).
 */
public interface RegisterUseCase {

    AuthTokens register(RegisterCommand command);
}
