package com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in;

import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

/**
 * Driving-side query for listing organizations: filter fields plus paging/sorting. Built by a driving
 * adapter from its transport-specific filter request.
 */
public record OrganizationSearchQuery(
        String name,
        Boolean isActive,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
) {
}
