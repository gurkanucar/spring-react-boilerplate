package com.gucardev.springreactboilerplate.features.core.role.application.port.out;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Output port: search roles with dynamic criteria. The persistence adapter translates the
 * {@link RoleSearchCriteria} into a query (Specification) and returns domain models.
 */
public interface SearchRolesPort {

    Page<Role> search(RoleSearchCriteria criteria, Pageable pageable);
}
