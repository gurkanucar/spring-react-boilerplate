package com.gucardev.springreactboilerplate.features.core.user.application.port.out;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import java.util.Optional;

/**
 * Output port: resolve a role by its name. Backed by a driven adapter that delegates to the role
 * feature (preserving its cached lookup), keeping the role persistence internals off the user core.
 */
public interface RoleLookupPort {

    Optional<Role> findByName(String name);
}
