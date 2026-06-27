package com.gucardev.springreactboilerplate.features.scheduledevent.service.usecase;

import com.gucardev.springreactboilerplate.features.scheduledevent.entity.ScheduledEvent;
import com.gucardev.springreactboilerplate.features.scheduledevent.entity.ScheduledEventStatus;
import com.gucardev.springreactboilerplate.features.scheduledevent.exception.ScheduledEventExceptionType;
import com.gucardev.springreactboilerplate.features.scheduledevent.mapper.ScheduledEventMapper;
import com.gucardev.springreactboilerplate.features.scheduledevent.model.dto.ScheduledEventResponse;
import com.gucardev.springreactboilerplate.features.scheduledevent.repository.ScheduledEventRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cancels a still-pending event. The delayed-message plugin can't recall an already-published
 * message, so cancelling just flips the row to {@code CANCELLED}; when the broker later delivers the
 * message, {@code ProcessScheduledEventUseCase} sees the status and skips the work (tombstone).
 */
@Service
@RequiredArgsConstructor
public class CancelScheduledEventUseCase {

    private final ScheduledEventFinder finder;
    private final ScheduledEventRepository repository;
    private final ScheduledEventMapper mapper;

    @Transactional
    public ScheduledEventResponse execute(UUID id) {
        ScheduledEvent event = finder.findById(id);
        if (event.getStatus() != ScheduledEventStatus.SCHEDULED) {
            throw ScheduledEventExceptionType.NOT_CANCELLABLE.toException(id, event.getStatus());
        }
        event.setStatus(ScheduledEventStatus.CANCELLED);
        event.setCancelledAt(Instant.now());
        return mapper.toDto(repository.save(event));
    }
}
