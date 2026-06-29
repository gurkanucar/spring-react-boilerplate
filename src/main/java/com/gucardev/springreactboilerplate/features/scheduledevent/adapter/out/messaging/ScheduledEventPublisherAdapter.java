package com.gucardev.springreactboilerplate.features.scheduledevent.adapter.out.messaging;

import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out.ScheduledEventPublisherPort;
import com.gucardev.springreactboilerplate.features.scheduledevent.domain.message.ScheduledEventMessage;
import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import com.gucardev.springreactboilerplate.infra.config.rabbitmq.RabbitMqConfig;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Driven (messaging-out) adapter implementing {@link ScheduledEventPublisherPort}. Publishes the event
 * to the delayed exchange: the {@code x-delay} header tells the {@code rabbitmq_delayed_message_exchange}
 * plugin how long (ms) to hold the message before it is routed to the bound queue — i.e. the message is
 * "scheduled" for future delivery, no broker-side timer or polling on our part.
 *
 * <p>Owns the outbox-like concern the application core must not know about: the message is published
 * only after the surrounding transaction commits, so a small delay (or delay=0) can't let the listener
 * load the row before the insert is visible; if the tx rolls back, nothing is sent.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledEventPublisherAdapter implements ScheduledEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(ScheduledEvent event) {
        ScheduledEventMessage message = new ScheduledEventMessage(
                event.getId().toString(), event.getEventType(), event.getScheduledAt(), event.getFireAt());
        long delayMillis = Duration.ofSeconds(event.getDelaySeconds()).toMillis();
        publishAfterCommit(message, delayMillis);
    }

    private void publishAfterCommit(ScheduledEventMessage message, long delayMillis) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    doPublish(message, delayMillis);
                }
            });
        } else {
            doPublish(message, delayMillis);
        }
    }

    private void doPublish(ScheduledEventMessage message, long delayMillis) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.DELAYED_EXCHANGE,
                RabbitMqConfig.SCHEDULED_EVENT_ROUTING_KEY,
                message,
                amqpMessage -> {
                    // Plugin holds the message for this many ms before routing it on.
                    amqpMessage.getMessageProperties().setHeader(RabbitMqConfig.X_DELAY_HEADER, delayMillis);
                    return amqpMessage;
                });
        log.info("[SCHEDULED-EVENT] published id={} delayMs={} fireAt={}",
                message.id(), delayMillis, message.fireAt());
    }
}
