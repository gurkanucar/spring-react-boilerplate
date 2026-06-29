package com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in;

import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Input port: list scheduled events, paged, with an optional status/date filter.
 */
public interface ListScheduledEventsUseCase {

    Page<ScheduledEvent> list(ScheduledEventCriteria criteria, Pageable pageable);
}
