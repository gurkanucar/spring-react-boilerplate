package com.gucardev.springreactboilerplate.features.order.adapter.in.messaging;

import com.gucardev.springreactboilerplate.features.order.application.port.in.HandleOrderCreatedUseCase;
import com.gucardev.springreactboilerplate.features.order.application.port.in.OrderCreatedEvent;
import com.gucardev.springreactboilerplate.features.order.domain.event.OrderEvents;
import com.gucardev.springreactboilerplate.features.outbox.model.message.OutboxEventEnvelope;
import com.gucardev.springreactboilerplate.infra.config.rabbitmq.OutboxRabbitConfig;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Driving (messaging-in) adapter: consumes order events the relay published. Runs on the dedicated
 * {@code outboxRabbitListenerContainerFactory} (retry with backoff, then dead-letter — see
 * {@code OutboxRabbitConfig}).
 *
 * <p>The listener stays thin: it translates the wire {@link OutboxEventEnvelope} into a typed
 * {@link OrderCreatedEvent} and delegates to the idempotent, transactional input port. Throwing here
 * triggers the factory's retry/DLQ handling; returning normally acks the message.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final HandleOrderCreatedUseCase handleOrderCreatedUseCase;

    @RabbitListener(queues = OutboxRabbitConfig.ORDER_EVENTS_QUEUE,
            containerFactory = OutboxRabbitConfig.LISTENER_FACTORY)
    public void onOrderEvent(OutboxEventEnvelope envelope) {
        log.info("[ORDER-CONSUMER] received eventId={} type={} aggregate={}:{}",
                envelope.eventId(), envelope.eventType(),
                envelope.aggregateType(), envelope.aggregateId());

        UUID eventId = UUID.fromString(envelope.eventId());
        UUID orderId = UUID.fromString(envelope.payload().get(OrderEvents.KEY_ORDER_ID).toString());
        String customerName = String.valueOf(envelope.payload().get(OrderEvents.KEY_CUSTOMER_NAME));

        handleOrderCreatedUseCase.handle(new OrderCreatedEvent(eventId, orderId, customerName));
    }
}
