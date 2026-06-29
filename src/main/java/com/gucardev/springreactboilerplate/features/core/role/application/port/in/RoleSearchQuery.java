package com.gucardev.springreactboilerplate.features.core.role.application.port.in;

import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

/**
 * Driving-side query for listing roles. Carries the filter inputs plus the resolved paging/sorting
 * from a driving adapter into the application core.
 */
public record RoleSearchQuery(
        String name,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
) {
}
