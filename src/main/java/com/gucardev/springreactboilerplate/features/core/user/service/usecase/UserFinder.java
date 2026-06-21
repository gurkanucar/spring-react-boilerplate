package com.gucardev.springreactboilerplate.features.core.user.service.usecase;

import com.gucardev.springreactboilerplate.features.core.user.entity.User;
import com.gucardev.springreactboilerplate.features.core.user.exception.UserExceptionType;
import com.gucardev.springreactboilerplate.features.core.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared lookup used by the read/update/delete use cases: returns the entity (with roles) or
 * throws a domain {@code NOT_FOUND}. Keeps the "fetch or 404" logic in one place.
 */
@Service
@RequiredArgsConstructor
public class UserFinder {

    private final UserRepository repository;

    public User findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> UserExceptionType.NOT_FOUND.toException(id));
    }
}
