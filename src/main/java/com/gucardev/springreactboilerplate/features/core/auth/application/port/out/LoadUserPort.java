package com.gucardev.springreactboilerplate.features.core.auth.application.port.out;

import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.Optional;

/**
 * Output port: load a user (enriched) by email. Backed by an adapter delegating to the user feature's
 * input port, keeping the auth core off the user internals.
 */
public interface LoadUserPort {

    Optional<User> findByEmail(String email);
}
