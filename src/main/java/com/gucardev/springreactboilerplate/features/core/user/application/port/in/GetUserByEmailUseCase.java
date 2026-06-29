package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.Optional;

/**
 * Input port: load a user by email enriched with resolved profile-image URLs (and roles). Used by
 * the auth feature for the login/refresh/register response bodies and {@code /auth/me}.
 */
public interface GetUserByEmailUseCase {

    Optional<User> findByEmail(String email);
}
