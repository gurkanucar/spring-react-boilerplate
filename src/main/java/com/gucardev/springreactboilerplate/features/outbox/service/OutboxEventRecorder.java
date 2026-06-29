package com.gucardev.springreactboilerplate.features.outbox.service;

import com.gucardev.springreactboilerplate.features.outbox.entity.OutboxMessage;
import com.gucardev.springreactboilerplate.features.outbox.entity.OutboxStatus;
import com.gucardev.springreactboilerplate.features.outbox.repository.OutboxMessageRepository;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * The producer-side entry point of the outbox pattern: appends an event to the outbox table.
 *
 * <p>Call this from inside a business use case's {@code @Transactional} method, right after the
 * business write. Because the insert here joins the caller's transaction, the business change and the
 * event are persisted atomically — the relay can then publish the event with the guarantee that the
 * business state it describes is already committed. This method must NOT publish to the broker: doing
 * I/O inside the business transaction is exactly what the outbox pattern exists to avoid.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventRecorder {

    private final OutboxMessageRepository repository;

    /**
     * Record an event to be published after the current transaction commits.
     *
     * @param aggregateType kind of aggregate the event is about (e.g. {@code "Order"})
     * @param aggregateId   aggregate instance id (e.g. the order id)
     * @param eventType     discriminator the consumer branches on (e.g. {@code "OrderCreated"})
     * @param routingKey    routing key the relay publishes the row under on the outbox exchange
     * @param payload       structured event body (stored as jsonb)
     * @return the persisted outbox row (its id is the event/idempotency key)
     */
    public OutboxMessage record(String aggregateType, String aggregateId, String eventType,
                                String routingKey, Map<String, Object> payload) {
        OutboxMessage message = repository.save(OutboxMessage.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .routingKey(routingKey)
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .attempts(0)
                // Eligible for the relay immediately; a failed publish later pushes this into the future.
                .nextAttemptAt(Instant.now())
                .build());
        log.debug("[OUTBOX] recorded event id={} type={} aggregate={}:{}",
                message.getId(), eventType, aggregateType, aggregateId);
        return message;
    }
}
