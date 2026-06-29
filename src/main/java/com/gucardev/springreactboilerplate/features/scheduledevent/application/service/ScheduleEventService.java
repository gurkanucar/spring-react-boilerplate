package com.gucardev.springreactboilerplate.features.scheduledevent.application.service;

import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.ScheduleEventCommand;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.ScheduleEventUseCase;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out.SaveScheduledEventPort;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out.ScheduledEventPublisherPort;
import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEventStatus;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Schedules an event for delayed delivery. The row is written {@code SCHEDULED} first, then the
 * publish output port is asked to send the (delayed) message. The publishing adapter only emits the
 * message after this transaction commits — a small delay (or delay=0) could otherwise let the
 * listener load the row before the insert is visible. If the tx rolls back, nothing is sent.
 */
@Service
@RequiredArgsConstructor
public class ScheduleEventService implements ScheduleEventUseCase {

    private final SaveScheduledEventPort saveScheduledEventPort;
    private final ScheduledEventPublisherPort scheduledEventPublisherPort;

    @Override
    @Transactional
    public ScheduledEvent schedule(ScheduleEventCommand command) {
        Instant now = Instant.now();
        Instant fireAt = now.plus(Duration.ofSeconds(command.delaySeconds()));

        ScheduledEvent event = saveScheduledEventPort.save(ScheduledEvent.builder()
                .eventType(command.eventType())
                .payload(command.payload())
                .status(ScheduledEventStatus.SCHEDULED)
                .delaySeconds(command.delaySeconds())
                .scheduledAt(now)
                .fireAt(fireAt)
                .attempts(0)
                .build());

        scheduledEventPublisherPort.publish(event);

        return event;
    }
}
