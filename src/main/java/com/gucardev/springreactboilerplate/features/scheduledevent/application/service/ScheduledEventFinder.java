package com.gucardev.springreactboilerplate.features.scheduledevent.application.service;

import com.gucardev.springreactboilerplate.features.scheduledevent.application.exception.ScheduledEventExceptionType;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out.LoadScheduledEventPort;
import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared "fetch or 404" lookup used by the read/cancel use cases.
 */
@Service
@RequiredArgsConstructor
public class ScheduledEventFinder {

    private final LoadScheduledEventPort loadScheduledEventPort;

    public ScheduledEvent findById(UUID id) {
        return loadScheduledEventPort.findById(id)
                .orElseThrow(() -> ScheduledEventExceptionType.NOT_FOUND.toException(id));
    }
}
