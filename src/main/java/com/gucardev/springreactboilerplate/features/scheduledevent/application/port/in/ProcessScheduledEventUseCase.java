package com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in;

/**
 * Input port: consumer-side handler for a released (delayed) scheduled event. Invoked by the
 * messaging-in adapter; the implementation is idempotent because delivery is at-least-once.
 */
public interface ProcessScheduledEventUseCase {

    void process(ProcessScheduledEventCommand command);
}
