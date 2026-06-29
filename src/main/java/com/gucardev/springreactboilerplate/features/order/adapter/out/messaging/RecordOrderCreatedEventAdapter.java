package com.gucardev.springreactboilerplate.features.order.adapter.out.messaging;

import com.gucardev.springreactboilerplate.features.order.application.port.out.RecordOrderCreatedEventPort;
import com.gucardev.springreactboilerplate.features.order.domain.event.OrderEvents;
import com.gucardev.springreactboilerplate.features.order.domain.model.Order;
import com.gucardev.springreactboilerplate.features.outbox.service.OutboxEventRecorder;
import com.gucardev.springreactboilerplate.infra.config.rabbitmq.OutboxRabbitConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter that records an {@code OrderCreated} event into the transactional outbox. It owns the
 * outbox mechanics the application core must not know about: the routing key, the payload shape, and
 * the call into {@link OutboxEventRecorder} (which enlists in the caller's transaction).
 */
@Component
@RequiredArgsConstructor
public class RecordOrderCreatedEventAdapter implements RecordOrderCreatedEventPort {

    private final OutboxEventRecorder outboxEventRecorder;

    @Override
    public void record(Order order) {
        outboxEventRecorder.record(
                OrderEvents.AGGREGATE_TYPE,
                order.getId().toString(),
                OrderEvents.ORDER_CREATED,
                OutboxRabbitConfig.ORDER_CREATED_ROUTING_KEY,
                buildPayload(order));
    }

    private Map<String, Object> buildPayload(Order order) {
        // LinkedHashMap keeps a stable key order in the stored jsonb / on-the-wire JSON.
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(OrderEvents.KEY_ORDER_ID, order.getId().toString());
        payload.put(OrderEvents.KEY_CUSTOMER_NAME, order.getCustomerName());
        payload.put(OrderEvents.KEY_PRODUCT, order.getProduct());
        payload.put(OrderEvents.KEY_QUANTITY, order.getQuantity().value());
        payload.put(OrderEvents.KEY_AMOUNT, order.getAmount().value());
        return payload;
    }
}
