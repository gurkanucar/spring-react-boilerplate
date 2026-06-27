package com.gucardev.springreactboilerplate.features.scheduledevent.publisher;

import com.gucardev.springreactboilerplate.features.scheduledevent.model.message.ScheduledEventMessage;
import com.gucardev.springreactboilerplate.infra.config.rabbitmq.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes an event to the delayed exchange. The {@code x-delay} header tells the
 * {@code rabbitmq_delayed_message_exchange} plugin how long (ms) to hold the message before it is
 * routed to the bound queue — i.e. the message is "scheduled" for future delivery, no broker-side
 * timer or polling on our part.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(ScheduledEventMessage message, long delayMillis) {
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
