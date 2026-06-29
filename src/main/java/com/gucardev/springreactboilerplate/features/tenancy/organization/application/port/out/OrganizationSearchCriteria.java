package com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out;

import java.time.LocalDate;

/**
 * Driven-side search criteria for organizations. Carries the filter values the persistence adapter
 * translates into a query.
 */
public record OrganizationSearchCriteria(
        String name,
        Boolean isActive,
        LocalDate startDate,
        LocalDate endDate
) {
}
