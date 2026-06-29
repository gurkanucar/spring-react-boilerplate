package com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in;

import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import java.util.UUID;

/**
 * Input port: cancel a still-pending scheduled event (tombstone it so the listener skips it).
 */
public interface CancelScheduledEventUseCase {

    ScheduledEvent cancel(UUID id);
}
