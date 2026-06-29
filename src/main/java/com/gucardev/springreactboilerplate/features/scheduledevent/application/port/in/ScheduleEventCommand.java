package com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in;

import java.util.Map;

/**
 * Driving-side command for scheduling an event. Carries already-validated input from a driving
 * adapter into the application core, decoupling the core from any particular transport.
 */
public record ScheduleEventCommand(
        String eventType,
        Map<String, Object> payload,
        Long delaySeconds
) {
}
