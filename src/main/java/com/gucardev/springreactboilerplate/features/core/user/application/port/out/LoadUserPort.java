package com.gucardev.springreactboilerplate.features.core.user.application.port.out;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port: load users (with roles) from the store. Implemented by a driven persistence adapter.
 */
public interface LoadUserPort {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
