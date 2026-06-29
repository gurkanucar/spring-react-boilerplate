package com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out;

import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;

/**
 * Output port: publish a scheduled event to the broker for delayed delivery. The application core
 * states the intent; the driven adapter owns the mechanism (delayed exchange, routing key, the
 * {@code x-delay} header, the wire message shape, and publishing only after the surrounding
 * transaction commits) so none of that infrastructure leaks into the core.
 */
public interface ScheduledEventPublisherPort {

    void publish(ScheduledEvent event);
}
