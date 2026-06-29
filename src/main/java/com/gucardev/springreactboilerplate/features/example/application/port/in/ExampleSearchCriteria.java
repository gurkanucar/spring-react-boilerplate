package com.gucardev.springreactboilerplate.features.example.application.port.in;

import java.time.LocalDate;

/**
 * Plain application-level search criteria for listing examples. A driving adapter maps its
 * transport-specific filter (e.g. {@code ExampleFilterRequest}) into this record; the persistence
 * adapter turns it into a query specification.
 */
public record ExampleSearchCriteria(
        String name,
        LocalDate startDate,
        LocalDate endDate
) {
}
