package com.gucardev.springreactboilerplate.features.order.service.usecase;

import com.gucardev.springreactboilerplate.features.order.entity.Order;
import com.gucardev.springreactboilerplate.features.order.entity.OrderStatus;
import com.gucardev.springreactboilerplate.features.order.event.OrderEvents;
import com.gucardev.springreactboilerplate.features.order.repository.OrderRepository;
import com.gucardev.springreactboilerplate.features.outbox.entity.ProcessedMessage;
import com.gucardev.springreactboilerplate.features.outbox.model.message.OutboxEventEnvelope;
import com.gucardev.springreactboilerplate.features.outbox.repository.ProcessedMessageRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consumer-side handler for {@code OrderCreated} — the "do the real work when the event arrives" half.
 *
 * <p>Idempotent by design, because delivery is at-least-once (the relay can re-publish after a crash;
 * the broker can redeliver). The whole method is one transaction:
 *
 * <ol>
 *   <li>if the event id is already in the inbox ({@code processed_messages}) → it was handled before
 *       → skip (this is what makes a duplicate harmless);</li>
 *   <li>otherwise do the side effect (here: confirm the order — stand-in for a real downstream action
 *       like calling an external service), then</li>
 *   <li>record the event id in the inbox.</li>
 * </ol>
 *
 * <p>The side effect and the inbox marker commit together, so a failure rolls back BOTH: the marker
 * isn't written, the message is redelivered, and the work is retried — never half-done.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HandleOrderCreatedUseCase {

    private final OrderRepository orderRepository;
    private final ProcessedMessageRepository processedMessageRepository;

    @Transactional
    public void execute(OutboxEventEnvelope envelope) {
        UUID eventId = UUID.fromString(envelope.eventId());

        if (processedMessageRepository.existsById(eventId)) {
            log.info("[ORDER-CONSUMER] event {} already processed — skipping (idempotent)", eventId);
            return;
        }

        // --- The actual work. This is where a real consumer would call a downstream/external service,
        // generate a report, send a notification, etc. Anything that throws here rolls the whole tx
        // back, so the inbox marker below is not written and the message is retried / dead-lettered.
        Object orderIdValue = envelope.payload().get(OrderEvents.KEY_ORDER_ID);
        UUID orderId = UUID.fromString(orderIdValue.toString());
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalStateException("OrderCreated for unknown order " + orderId));
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        log.info("[ORDER-CONSUMER] confirmed order {} for customer '{}'",
                orderId, envelope.payload().get(OrderEvents.KEY_CUSTOMER_NAME));

        // --- Mark processed (same tx as the work above) so a redelivery is skipped at step 1.
        processedMessageRepository.save(ProcessedMessage.builder()
                .id(eventId)
                .consumer(OrderEvents.ORDER_CREATED_CONSUMER)
                .processedAt(Instant.now())
                .build());
    }
}
