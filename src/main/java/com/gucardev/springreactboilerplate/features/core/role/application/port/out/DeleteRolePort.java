package com.gucardev.springreactboilerplate.features.core.role.application.port.out;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;

/**
 * Output port: delete a role. Implemented by a driven persistence adapter.
 */
public interface DeleteRolePort {

    void delete(Role role);
}
