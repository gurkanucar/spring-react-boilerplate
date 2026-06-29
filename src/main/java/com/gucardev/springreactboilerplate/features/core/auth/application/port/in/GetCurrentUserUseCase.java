package com.gucardev.springreactboilerplate.features.core.auth.application.port.in;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;

/**
 * Input port: return the currently authenticated user (resolved from the security context), enriched
 * for the client.
 */
public interface GetCurrentUserUseCase {

    User getCurrentUser();
}
