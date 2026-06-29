package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.user.application.port.in.LoadUserByEmailUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.LoadUserPort;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads a user by email with roles but without profile-image enrichment — the cheap path for
 * building a security principal on the authentication hot path.
 */
@Service
@RequiredArgsConstructor
public class LoadUserByEmailService implements LoadUserByEmailUseCase {

    private final LoadUserPort loadUserPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> loadByEmail(String email) {
        return loadUserPort.findByEmail(email);
    }
}
