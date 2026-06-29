package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.user.application.port.in.GetUserByEmailUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.LoadUserPort;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads a user by email and enriches it with profile-image URLs — the client-facing read used by the
 * auth feature for login/refresh/register responses and {@code /auth/me}.
 */
@Service
@RequiredArgsConstructor
public class GetUserByEmailService implements GetUserByEmailUseCase {

    private final LoadUserPort loadUserPort;
    private final UserImageEnricher imageEnricher;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        Optional<User> user = loadUserPort.findByEmail(email);
        user.ifPresent(imageEnricher::enrich);
        return user;
    }
}
