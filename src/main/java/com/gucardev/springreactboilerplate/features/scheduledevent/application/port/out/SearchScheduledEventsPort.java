package com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out;

import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.ScheduledEventCriteria;
import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Output port: paged search over scheduled events. The driven persistence adapter owns the
 * {@code Specification} translation; the application core only states the criteria.
 */
public interface SearchScheduledEventsPort {

    Page<ScheduledEvent> search(ScheduledEventCriteria criteria, Pageable pageable);
}
