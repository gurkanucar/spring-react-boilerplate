package com.gucardev.springreactboilerplate.features.core.role.application.port.out;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;

/**
 * Output port: persist a role (insert or update) and return the stored state, including any
 * generated id and audit metadata.
 */
public interface SaveRolePort {

    Role save(Role role);
}
