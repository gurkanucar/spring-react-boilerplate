package com.gucardev.springreactboilerplate.features.order.application.service;

import com.gucardev.springreactboilerplate.features.order.application.port.in.HandleOrderCreatedUseCase;
import com.gucardev.springreactboilerplate.features.order.application.port.in.OrderCreatedEvent;
import com.gucardev.springreactboilerplate.features.order.application.port.out.LoadOrderPort;
import com.gucardev.springreactboilerplate.features.order.application.port.out.ProcessedEventPort;
import com.gucardev.springreactboilerplate.features.order.application.port.out.SaveOrderPort;
import com.gucardev.springreactboilerplate.features.order.domain.event.OrderEvents;
import com.gucardev.springreactboilerplate.features.order.domain.model.Order;
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
 *   <li>if the event id is already in the inbox → it was handled before → skip (this is what makes a
 *       duplicate harmless);</li>
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
public class HandleOrderCreatedService implements HandleOrderCreatedUseCase {

    private final LoadOrderPort loadOrderPort;
    private final SaveOrderPort saveOrderPort;
    private final ProcessedEventPort processedEventPort;

    @Override
    @Transactional
    public void handle(OrderCreatedEvent event) {
        if (processedEventPort.isProcessed(event.eventId())) {
            log.info("[ORDER-CONSUMER] event {} already processed — skipping (idempotent)", event.eventId());
            return;
        }

        // --- The actual work. This is where a real consumer would call a downstream/external service,
        // generate a report, send a notification, etc. Anything that throws here rolls the whole tx
        // back, so the inbox marker below is not written and the message is retried / dead-lettered.
        Order order = loadOrderPort.findById(event.orderId()).orElseThrow(
                () -> new IllegalStateException("OrderCreated for unknown order " + event.orderId()));
        order.confirm();
        saveOrderPort.save(order);
        log.info("[ORDER-CONSUMER] confirmed order {} for customer '{}'",
                event.orderId(), event.customerName());

        // --- Mark processed (same tx as the work above) so a redelivery is skipped at step 1.
        processedEventPort.markProcessed(event.eventId(), OrderEvents.ORDER_CREATED_CONSUMER);
    }
}
