package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

/**
 * Driving-side query for listing workspaces. {@code organizationId} is the requested org filter
 * (honoured only for a super-admin; org users are always constrained to their own org by the use
 * case). Carries the already-built {@link Pageable} from the driving adapter.
 */
public record ListWorkspacesQuery(
        String name,
        Boolean isActive,
        UUID organizationId,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
) {
}
