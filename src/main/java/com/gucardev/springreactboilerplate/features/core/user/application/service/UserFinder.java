package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.user.application.exception.UserExceptionType;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.LoadUserPort;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared "fetch or 404" lookup used by the read/update/delete use cases: returns the domain model
 * (with roles) or throws a domain {@code NOT_FOUND}. Keeps the lookup logic in one place.
 */
@Service
@RequiredArgsConstructor
public class UserFinder {

    private final LoadUserPort loadUserPort;

    public User findById(UUID id) {
        return loadUserPort.findById(id)
                .orElseThrow(() -> UserExceptionType.NOT_FOUND.toException(id));
    }
}
