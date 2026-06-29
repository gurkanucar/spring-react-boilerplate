package com.gucardev.springreactboilerplate.features.order.application.service;

import com.gucardev.springreactboilerplate.features.order.application.port.in.PlaceOrderCommand;
import com.gucardev.springreactboilerplate.features.order.application.port.in.PlaceOrderUseCase;
import com.gucardev.springreactboilerplate.features.order.application.port.out.RecordOrderCreatedEventPort;
import com.gucardev.springreactboilerplate.features.order.application.port.out.SaveOrderPort;
import com.gucardev.springreactboilerplate.features.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Places an order — the producer side of the outbox demo.
 *
 * <p>The whole method is one transaction: it saves the {@link Order} AND records an
 * {@code OrderCreated} event (via the {@link RecordOrderCreatedEventPort} output port) into the outbox.
 * Both commit together, so there is no window where the order exists without its event (or vice versa),
 * and — crucially — there is no broker call on this request path. The relay publishes the event
 * afterwards. If the broker is down right now, the order still succeeds and the event goes out once the
 * relay can reach the broker.
 */
@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {

    private final SaveOrderPort saveOrderPort;
    private final RecordOrderCreatedEventPort recordOrderCreatedEventPort;

    @Override
    @Transactional
    public Order place(PlaceOrderCommand command) {
        // The aggregate's factory enforces every creation invariant — the service just orchestrates.
        Order order = saveOrderPort.save(Order.place(
                command.customerName(),
                command.product(),
                command.quantity(),
                command.amount()));

        recordOrderCreatedEventPort.record(order);

        return order;
    }
}
