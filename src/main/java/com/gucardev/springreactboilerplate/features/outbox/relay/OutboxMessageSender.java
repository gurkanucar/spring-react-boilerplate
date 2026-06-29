package com.gucardev.springreactboilerplate.features.outbox.relay;

import com.gucardev.springreactboilerplate.features.outbox.config.OutboxProperties;
import com.gucardev.springreactboilerplate.features.outbox.entity.OutboxMessage;
import com.gucardev.springreactboilerplate.features.outbox.entity.OutboxStatus;
import com.gucardev.springreactboilerplate.features.outbox.model.message.OutboxEventEnvelope;
import com.gucardev.springreactboilerplate.features.outbox.repository.OutboxMessageRepository;
import com.gucardev.springreactboilerplate.infra.config.rabbitmq.OutboxRabbitConfig;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Publishes a single outbox row and records the outcome — each call in its OWN transaction
 * ({@code REQUIRES_NEW}) so one poisoned row can't roll back the progress made on its neighbours in
 * the same poll.
 *
 * <p>On success the row is flipped to {@code PUBLISHED}. On failure the exception is caught (not
 * rethrown) so the bookkeeping — {@code attempts}, {@code lastError}, a pushed-out {@code nextAttemptAt}
 * for backoff — still commits; the row stays {@code PENDING} for a later retry until it exhausts the
 * attempt budget, at which point it becomes {@code FAILED}.
 *
 * <p>Delivery is at-least-once: {@code convertAndSend} returning without throwing means the broker
 * accepted the message, but a crash after the broker accepts yet before this tx commits would
 * re-publish the row on the next poll. That's why the consumer dedupes on {@code eventId}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxMessageSender {

    private final OutboxMessageRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final OutboxProperties properties;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(UUID id) {
        OutboxMessage message = repository.findById(id).orElse(null);
        if (message == null || message.getStatus() != OutboxStatus.PENDING) {
            // Already handled by an earlier iteration, or vanished — nothing to do.
            return;
        }

        try {
            publish(message);
            message.setStatus(OutboxStatus.PUBLISHED);
            message.setPublishedAt(Instant.now());
            message.setLastError(null);
            log.info("[OUTBOX] published id={} type={} routingKey={}",
                    message.getId(), message.getEventType(), message.getRoutingKey());
        } catch (AmqpException e) {
            recordFailure(message, e);
        }
        repository.save(message);
    }

    private void publish(OutboxMessage message) {
        OutboxEventEnvelope envelope = new OutboxEventEnvelope(
                message.getId().toString(),
                message.getAggregateType(),
                message.getAggregateId(),
                message.getEventType(),
                message.getPayload(),
                message.getCreatedAt() == null ? Instant.now()
                        : message.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant());

        rabbitTemplate.convertAndSend(
                OutboxRabbitConfig.OUTBOX_EXCHANGE,
                message.getRoutingKey(),
                envelope,
                amqp -> {
                    // Stamp the event id as the AMQP messageId for broker-level tracing/dedup tooling.
                    amqp.getMessageProperties().setMessageId(envelope.eventId());
                    return amqp;
                });
    }

    private void recordFailure(OutboxMessage message, Exception e) {
        int attempts = message.getAttempts() + 1;
        message.setAttempts(attempts);
        message.setLastError(truncate(e.getMessage()));
        if (attempts >= properties.getMaxAttempts()) {
            message.setStatus(OutboxStatus.FAILED);
            log.error("[OUTBOX] giving up on id={} after {} attempts — marked FAILED",
                    message.getId(), attempts, e);
        } else {
            // Linear backoff: wait longer the more it has failed, so a flaky broker isn't hammered.
            message.setNextAttemptAt(Instant.now().plus(
                    Duration.ofSeconds(properties.getBackoffSeconds() * attempts)));
            log.warn("[OUTBOX] publish failed for id={} attempt={} — retrying after backoff: {}",
                    message.getId(), attempts, e.getMessage());
        }
    }

    private String truncate(String value) {
        if (value == null) {
            return null;
        }
        return value.length() <= 1000 ? value : value.substring(0, 1000);
    }
}
