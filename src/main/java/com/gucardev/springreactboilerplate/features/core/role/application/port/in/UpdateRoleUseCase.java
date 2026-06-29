package com.gucardev.springreactboilerplate.features.core.role.application.port.in;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;

/**
 * Input port: update a role's descriptive fields (the name is immutable).
 */
public interface UpdateRoleUseCase {

    Role update(Long id, UpdateRoleCommand command);
}
