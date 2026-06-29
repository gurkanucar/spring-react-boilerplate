package com.gucardev.springreactboilerplate.features.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gucardev.springreactboilerplate.features.order.entity.Order;
import com.gucardev.springreactboilerplate.features.order.entity.OrderStatus;
import com.gucardev.springreactboilerplate.features.order.event.OrderEvents;
import com.gucardev.springreactboilerplate.features.order.repository.OrderRepository;
import com.gucardev.springreactboilerplate.features.order.service.usecase.HandleOrderCreatedUseCase;
import com.gucardev.springreactboilerplate.features.outbox.entity.ProcessedMessage;
import com.gucardev.springreactboilerplate.features.outbox.model.message.OutboxEventEnvelope;
import com.gucardev.springreactboilerplate.features.outbox.repository.ProcessedMessageRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The consumer handler's idempotency contract: a first delivery confirms the order and records the
 * event id; a redelivery of the same event id is skipped without touching the order again.
 */
@ExtendWith(MockitoExtension.class)
class HandleOrderCreatedUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProcessedMessageRepository processedMessageRepository;

    @InjectMocks
    private HandleOrderCreatedUseCase useCase;

    private OutboxEventEnvelope envelopeFor(UUID eventId, UUID orderId) {
        return new OutboxEventEnvelope(
                eventId.toString(), OrderEvents.AGGREGATE_TYPE, orderId.toString(), OrderEvents.ORDER_CREATED,
                Map.of(OrderEvents.KEY_ORDER_ID, orderId.toString(), OrderEvents.KEY_CUSTOMER_NAME, "Ada"),
                Instant.now());
    }

    private Order placedOrder(UUID orderId) {
        return Order.builder()
                .id(orderId).customerName("Ada").product("Keyboard")
                .quantity(1).amount(new BigDecimal("10.00")).status(OrderStatus.PLACED)
                .build();
    }

    @Test
    void firstDelivery_confirmsOrder_andRecordsInbox() {
        UUID eventId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Order order = placedOrder(orderId);
        when(processedMessageRepository.existsById(eventId)).thenReturn(false);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.execute(envelopeFor(eventId, orderId));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(orderRepository).save(order);
        verify(processedMessageRepository).save(any(ProcessedMessage.class));
    }

    @Test
    void redelivery_isSkipped() {
        UUID eventId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        when(processedMessageRepository.existsById(eventId)).thenReturn(true);

        useCase.execute(envelopeFor(eventId, orderId));

        verify(orderRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
        verify(processedMessageRepository, never()).save(any());
    }
}
