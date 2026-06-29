package com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in;

import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEventStatus;
import java.time.LocalDate;

/**
 * Transport-agnostic search criteria for listing scheduled events. A driving adapter translates its
 * filter DTO into this record; paging/sorting is passed separately as a {@code Pageable}.
 */
public record ScheduledEventCriteria(
        ScheduledEventStatus status,
        LocalDate startDate,
        LocalDate endDate
) {
}
