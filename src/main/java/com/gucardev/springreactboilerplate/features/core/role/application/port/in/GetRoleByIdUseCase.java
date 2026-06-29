package com.gucardev.springreactboilerplate.features.core.role.application.port.in;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;

/**
 * Input port: read a single role by id.
 */
public interface GetRoleByIdUseCase {

    Role getById(Long id);
}
