package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.Optional;

/**
 * Input port: load a user by email with roles but WITHOUT profile-image URL enrichment — the cheap
 * path used to build a security principal on the hot authentication path (avoids a per-request file
 * lookup). Distinct from {@link GetUserByEmailUseCase}, which enriches for client-facing responses.
 */
public interface LoadUserByEmailUseCase {

    Optional<User> loadByEmail(String email);
}
