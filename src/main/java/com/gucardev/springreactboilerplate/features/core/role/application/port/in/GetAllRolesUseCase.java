package com.gucardev.springreactboilerplate.features.core.role.application.port.in;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import org.springframework.data.domain.Page;

/**
 * Input port: list roles (paged, sorted and filtered), returning domain models.
 */
public interface GetAllRolesUseCase {

    Page<Role> getAll(RoleSearchQuery query);
}
