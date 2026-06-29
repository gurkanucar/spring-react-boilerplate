package com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in;

import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;

/**
 * Input port: schedule an event for delayed delivery via RabbitMQ. Driving adapters depend on this
 * interface, not on the implementing service.
 */
public interface ScheduleEventUseCase {

    ScheduledEvent schedule(ScheduleEventCommand command);
}
