package com.gucardev.springreactboilerplate.features.core.role.application.port.out;

import java.time.LocalDate;

/**
 * Driven-side filter criteria for searching roles, decoupling the search port from any particular
 * transport-level filter DTO.
 */
public record RoleSearchCriteria(
        String name,
        LocalDate startDate,
        LocalDate endDate
) {
}
