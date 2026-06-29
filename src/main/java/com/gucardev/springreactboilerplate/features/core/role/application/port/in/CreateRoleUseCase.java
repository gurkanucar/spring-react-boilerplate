package com.gucardev.springreactboilerplate.features.core.role.application.port.in;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;

/**
 * Input port: create a role. Driving adapters depend on this interface, not on the implementing
 * service.
 */
public interface CreateRoleUseCase {

    Role create(CreateRoleCommand command);
}
