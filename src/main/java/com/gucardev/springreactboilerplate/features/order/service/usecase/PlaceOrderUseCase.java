package com.gucardev.springreactboilerplate.features.order.service.usecase;

import com.gucardev.springreactboilerplate.features.order.entity.Order;
import com.gucardev.springreactboilerplate.features.order.entity.OrderStatus;
import com.gucardev.springreactboilerplate.features.order.event.OrderEvents;
import com.gucardev.springreactboilerplate.features.order.mapper.OrderMapper;
import com.gucardev.springreactboilerplate.features.order.model.dto.OrderResponse;
import com.gucardev.springreactboilerplate.features.order.model.request.PlaceOrderRequest;
import com.gucardev.springreactboilerplate.features.order.repository.OrderRepository;
import com.gucardev.springreactboilerplate.features.outbox.service.OutboxEventRecorder;
import com.gucardev.springreactboilerplate.infra.config.rabbitmq.OutboxRabbitConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Places an order — the producer side of the outbox demo.
 *
 * <p>The whole method is one transaction: it saves the {@link Order} AND records an
 * {@code OrderCreated} event into the outbox. Both commit together, so there is no window where the
 * order exists without its event (or vice versa), and — crucially — there is no broker call on this
 * request path. The relay publishes the event afterwards. If the broker is down right now, the order
 * still succeeds and the event goes out once the relay can reach the broker.
 */
@Service
@RequiredArgsConstructor
public class PlaceOrderUseCase {

    private final OrderRepository orderRepository;
    private final OutboxEventRecorder outboxEventRecorder;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse execute(PlaceOrderRequest request) {
        Order order = orderRepository.save(Order.builder()
                .customerName(request.customerName())
                .product(request.product())
                .quantity(request.quantity())
                .amount(request.amount())
                .status(OrderStatus.PLACED)
                .build());

        outboxEventRecorder.record(
                OrderEvents.AGGREGATE_TYPE,
                order.getId().toString(),
                OrderEvents.ORDER_CREATED,
                OutboxRabbitConfig.ORDER_CREATED_ROUTING_KEY,
                buildPayload(order));

        return orderMapper.toDto(order);
    }

    private Map<String, Object> buildPayload(Order order) {
        // LinkedHashMap keeps a stable key order in the stored jsonb / on-the-wire JSON.
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(OrderEvents.KEY_ORDER_ID, order.getId().toString());
        payload.put(OrderEvents.KEY_CUSTOMER_NAME, order.getCustomerName());
        payload.put(OrderEvents.KEY_PRODUCT, order.getProduct());
        payload.put(OrderEvents.KEY_QUANTITY, order.getQuantity());
        payload.put(OrderEvents.KEY_AMOUNT, order.getAmount());
        return payload;
    }
}
