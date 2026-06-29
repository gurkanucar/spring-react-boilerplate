package com.gucardev.springreactboilerplate.features.core.auth.adapter.out.user;

import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.LoadUserPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.GetUserByEmailUseCase;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the auth {@link LoadUserPort} by delegating to the user feature's input
 * port. This is one of the two places the auth module depends on user types, keeping the auth core
 * decoupled from the user internals.
 */
@Component
@RequiredArgsConstructor
public class LoadUserAdapter implements LoadUserPort {

    private final GetUserByEmailUseCase getUserByEmailUseCase;

    @Override
    public Optional<User> findByEmail(String email) {
        return getUserByEmailUseCase.findByEmail(email);
    }
}
