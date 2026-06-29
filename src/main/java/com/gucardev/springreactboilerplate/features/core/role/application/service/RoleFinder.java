package com.gucardev.springreactboilerplate.features.core.role.application.service;

import com.gucardev.springreactboilerplate.features.core.role.application.exception.RoleExceptionType;
import com.gucardev.springreactboilerplate.features.core.role.application.port.out.LoadRolePort;
import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared "fetch or 404" lookup used by the read/update/delete use cases: returns the domain model
 * or throws a domain {@code NOT_FOUND}. Keeps the lookup logic in one place.
 */
@Service
@RequiredArgsConstructor
public class RoleFinder {

    private final LoadRolePort loadRolePort;

    public Role findById(Long id) {
        return loadRolePort.findById(id)
                .orElseThrow(() -> RoleExceptionType.NOT_FOUND.toException(id));
    }
}
