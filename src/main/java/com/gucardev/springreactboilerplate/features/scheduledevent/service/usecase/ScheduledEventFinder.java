package com.gucardev.springreactboilerplate.features.scheduledevent.service.usecase;

import com.gucardev.springreactboilerplate.features.scheduledevent.entity.ScheduledEvent;
import com.gucardev.springreactboilerplate.features.scheduledevent.exception.ScheduledEventExceptionType;
import com.gucardev.springreactboilerplate.features.scheduledevent.repository.ScheduledEventRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared "fetch or 404" lookup used by the read/cancel use cases.
 */
@Service
@RequiredArgsConstructor
public class ScheduledEventFinder {

    private final ScheduledEventRepository repository;

    public ScheduledEvent findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> ScheduledEventExceptionType.NOT_FOUND.toException(id));
    }
}
