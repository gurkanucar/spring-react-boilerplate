package com.gucardev.springreactboilerplate.features.core.user.application.port.out;

import java.time.LocalDate;

/**
 * Driven-side filter criteria for searching users, decoupling the search port from any particular
 * transport-level filter DTO.
 */
public record UserSearchCriteria(
        String email,
        String name,
        Boolean activated,
        Boolean isActive,
        LocalDate startDate,
        LocalDate endDate
) {
}
