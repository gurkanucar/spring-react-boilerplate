package com.gucardev.springreactboilerplate.features.core.role.application.port.out;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import java.util.Optional;

/**
 * Output port: load roles from the store. Implemented by a driven persistence adapter.
 */
public interface LoadRolePort {

    Optional<Role> findById(Long id);

    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}
