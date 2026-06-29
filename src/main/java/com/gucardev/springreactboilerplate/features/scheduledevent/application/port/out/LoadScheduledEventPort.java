package com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out;

import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port: load a scheduled event from the store. Implemented by a driven persistence adapter.
 */
public interface LoadScheduledEventPort {

    Optional<ScheduledEvent> findById(UUID id);
}
