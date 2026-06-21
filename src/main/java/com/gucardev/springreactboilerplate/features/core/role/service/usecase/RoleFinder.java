package com.gucardev.springreactboilerplate.features.core.role.service.usecase;

import com.gucardev.springreactboilerplate.features.core.role.entity.Role;
import com.gucardev.springreactboilerplate.features.core.role.exception.RoleExceptionType;
import com.gucardev.springreactboilerplate.features.core.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared lookup used by the read/update/delete use cases: returns the entity or throws a domain
 * {@code NOT_FOUND}. Keeps the "fetch or 404" logic in one place.
 */
@Service
@RequiredArgsConstructor
public class RoleFinder {

    private final RoleRepository repository;

    public Role findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> RoleExceptionType.NOT_FOUND.toException(id));
    }
}
