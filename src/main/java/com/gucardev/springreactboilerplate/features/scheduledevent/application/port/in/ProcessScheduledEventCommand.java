package com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in;

import java.util.UUID;

/**
 * Driving-side command for processing a released scheduled event. The messaging-in adapter translates
 * the raw wire message into this typed command so the application core never touches wire formats.
 *
 * @param id the {@code ScheduledEvent} id to drive through its terminal state
 */
public record ProcessScheduledEventCommand(
        UUID id
) {
}
