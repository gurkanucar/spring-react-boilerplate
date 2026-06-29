package com.gucardev.springreactboilerplate.features.scheduledevent.application.service;

import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.GetScheduledEventUseCase;
import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetScheduledEventService implements GetScheduledEventUseCase {

    private final ScheduledEventFinder finder;

    @Override
    @Transactional(readOnly = true)
    public ScheduledEvent getById(UUID id) {
        return finder.findById(id);
    }
}
