package com.gucardev.springreactboilerplate.features.core.role.application.port.in;

/**
 * Input port: delete a role by id.
 */
public interface DeleteRoleUseCase {

    void delete(Long id);
}
