package com.gucardev.springreactboilerplate.features.scheduledevent.application.service;

import com.gucardev.springreactboilerplate.features.scheduledevent.application.exception.ScheduledEventExceptionType;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.CancelScheduledEventUseCase;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out.SaveScheduledEventPort;
import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cancels a still-pending event. The delayed-message plugin can't recall an already-published
 * message, so cancelling just flips the row to {@code CANCELLED}; when the broker later delivers the
 * message, {@code ProcessScheduledEventService} sees the status and skips the work (tombstone).
 */
@Service
@RequiredArgsConstructor
public class CancelScheduledEventService implements CancelScheduledEventUseCase {

    private final ScheduledEventFinder finder;
    private final SaveScheduledEventPort saveScheduledEventPort;

    @Override
    @Transactional
    public ScheduledEvent cancel(UUID id) {
        ScheduledEvent event = finder.findById(id);
        if (!event.isCancellable()) {
            throw ScheduledEventExceptionType.NOT_CANCELLABLE.toException(id, event.getStatus());
        }
        event.cancel();
        return saveScheduledEventPort.save(event);
    }
}
