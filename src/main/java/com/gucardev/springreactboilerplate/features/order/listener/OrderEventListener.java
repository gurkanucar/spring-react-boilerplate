package com.gucardev.springreactboilerplate.features.order.listener;

import com.gucardev.springreactboilerplate.features.order.service.usecase.HandleOrderCreatedUseCase;
import com.gucardev.springreactboilerplate.features.outbox.model.message.OutboxEventEnvelope;
import com.gucardev.springreactboilerplate.infra.config.rabbitmq.OutboxRabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes order events the relay published. Runs on the dedicated {@code outboxRabbitListenerContainerFactory}
 * (retry with backoff, then dead-letter — see {@code OutboxRabbitConfig}).
 *
 * <p>The listener stays thin: it converts the JSON body to an {@link OutboxEventEnvelope} and delegates
 * to the idempotent, transactional {@link HandleOrderCreatedUseCase}. Throwing here triggers the
 * factory's retry/DLQ handling; returning normally acks the message.
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
        handleOrderCreatedUseCase.execute(envelope);
    }
}
