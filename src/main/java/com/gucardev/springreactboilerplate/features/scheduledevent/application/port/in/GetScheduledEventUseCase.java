package com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in;

import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import java.util.UUID;

/**
 * Input port: read a single scheduled event and its lifecycle status.
 */
public interface GetScheduledEventUseCase {

    ScheduledEvent getById(UUID id);
}
