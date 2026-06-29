package com.gucardev.springreactboilerplate.features.core.auth.application.port.in;

/**
 * Input port: revoke a refresh token (logout).
 */
public interface LogoutUseCase {

    void logout(String refreshToken);
}
