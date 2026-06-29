package com.gucardev.springreactboilerplate.features.core.user.application.port.in;

import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

/**
 * Driving-side query for listing users. Carries the filter inputs plus the resolved paging/sorting
 * from a driving adapter into the application core.
 */
public record UserSearchQuery(
        String email,
        String name,
        Boolean activated,
        Boolean isActive,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
) {
}
