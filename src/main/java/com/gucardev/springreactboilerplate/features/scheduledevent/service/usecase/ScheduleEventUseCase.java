package com.gucardev.springreactboilerplate.features.scheduledevent.service.usecase;

import com.gucardev.springreactboilerplate.features.scheduledevent.entity.ScheduledEvent;
import com.gucardev.springreactboilerplate.features.scheduledevent.entity.ScheduledEventStatus;
import com.gucardev.springreactboilerplate.features.scheduledevent.mapper.ScheduledEventMapper;
import com.gucardev.springreactboilerplate.features.scheduledevent.model.dto.ScheduledEventResponse;
import com.gucardev.springreactboilerplate.features.scheduledevent.model.message.ScheduledEventMessage;
import com.gucardev.springreactboilerplate.features.scheduledevent.model.request.ScheduleEventRequest;
import com.gucardev.springreactboilerplate.features.scheduledevent.publisher.ScheduledEventPublisher;
import com.gucardev.springreactboilerplate.features.scheduledevent.repository.ScheduledEventRepository;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class ScheduleEventUseCase {

    private final ScheduledEventRepository repository;
    private final ScheduledEventPublisher publisher;
    private final ScheduledEventMapper mapper;

    @Transactional
    public ScheduledEventResponse execute(ScheduleEventRequest request) {
        Instant now = Instant.now();
        Instant fireAt = now.plus(Duration.ofSeconds(request.delaySeconds()));

        ScheduledEvent entity = repository.save(ScheduledEvent.builder()
                .eventType(request.eventType())
                .payload(request.payload())
                .status(ScheduledEventStatus.SCHEDULED)
                .delaySeconds(request.delaySeconds())
                .scheduledAt(now)
                .fireAt(fireAt)
                .attempts(0)
                .build());

        ScheduledEventMessage message =
                new ScheduledEventMessage(entity.getId().toString(), entity.getEventType(), now, fireAt);
        long delayMillis = Duration.ofSeconds(request.delaySeconds()).toMillis();

        // Publish only after this tx commits: a small delay (or delay=0) could otherwise let the
        // listener load the row before the insert is visible. If the tx rolls back, nothing is sent.
        publishAfterCommit(message, delayMillis);

        return mapper.toDto(entity);
    }

    private void publishAfterCommit(ScheduledEventMessage message, long delayMillis) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publisher.publish(message, delayMillis);
                }
            });
        } else {
            publisher.publish(message, delayMillis);
        }
    }
}
