package com.gucardev.springreactboilerplate.features.scheduledevent.application.service;

import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.ListScheduledEventsUseCase;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.ScheduledEventCriteria;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out.SearchScheduledEventsPort;
import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListScheduledEventsService implements ListScheduledEventsUseCase {

    private final SearchScheduledEventsPort searchScheduledEventsPort;

    @Override
    @Transactional(readOnly = true)
    public Page<ScheduledEvent> list(ScheduledEventCriteria criteria, Pageable pageable) {
        return searchScheduledEventsPort.search(criteria, pageable);
    }
}
