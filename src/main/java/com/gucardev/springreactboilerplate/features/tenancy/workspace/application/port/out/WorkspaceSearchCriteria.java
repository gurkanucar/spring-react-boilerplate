package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

/**
 * Criteria for {@link SearchWorkspacePort}. {@code organizationId} is the already-resolved tenant
 * scope to constrain to; {@code null} means no org constraint (only a global super-admin should reach
 * the store with null).
 */
public record WorkspaceSearchCriteria(
        String name,
        Boolean isActive,
        UUID organizationId,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
) {
}
