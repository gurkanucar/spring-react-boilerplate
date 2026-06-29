package com.gucardev.springreactboilerplate.features.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gucardev.springreactboilerplate.features.order.application.port.in.OrderCreatedEvent;
import com.gucardev.springreactboilerplate.features.order.application.port.out.LoadOrderPort;
import com.gucardev.springreactboilerplate.features.order.application.port.out.ProcessedEventPort;
import com.gucardev.springreactboilerplate.features.order.application.port.out.SaveOrderPort;
import com.gucardev.springreactboilerplate.features.order.application.service.HandleOrderCreatedService;
import com.gucardev.springreactboilerplate.features.order.domain.event.OrderEvents;
import com.gucardev.springreactboilerplate.features.order.domain.model.Order;
import com.gucardev.springreactboilerplate.features.order.domain.model.OrderStatus;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The consumer handler's idempotency contract, exercised through the application service and its
 * output ports: a first delivery confirms the order and records the event id; a redelivery of the
 * same event id is skipped without touching the order again.
 */
@ExtendWith(MockitoExtension.class)
class HandleOrderCreatedUseCaseTest {

    @Mock
    private LoadOrderPort loadOrderPort;

    @Mock
    private SaveOrderPort saveOrderPort;

    @Mock
    private ProcessedEventPort processedEventPort;

    @InjectMocks
    private HandleOrderCreatedService service;

    private OrderCreatedEvent eventFor(UUID eventId, UUID orderId) {
        return new OrderCreatedEvent(eventId, orderId, "Ada");
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
        when(processedEventPort.isProcessed(eventId)).thenReturn(false);
        when(loadOrderPort.findById(orderId)).thenReturn(Optional.of(order));

        service.handle(eventFor(eventId, orderId));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(saveOrderPort).save(order);
        verify(processedEventPort).markProcessed(eventId, OrderEvents.ORDER_CREATED_CONSUMER);
    }

    @Test
    void redelivery_isSkipped() {
        UUID eventId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        when(processedEventPort.isProcessed(eventId)).thenReturn(true);

        service.handle(eventFor(eventId, orderId));

        verify(loadOrderPort, never()).findById(any());
        verify(saveOrderPort, never()).save(any());
        verify(processedEventPort, never()).markProcessed(any(), eq(OrderEvents.ORDER_CREATED_CONSUMER));
    }
}
