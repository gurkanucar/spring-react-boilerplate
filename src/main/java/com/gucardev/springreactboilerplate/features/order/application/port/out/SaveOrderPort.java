package com.gucardev.springreactboilerplate.features.order.application.port.out;

import com.gucardev.springreactboilerplate.features.order.domain.model.Order;

/**
 * Output port: persist an order (insert or update) and return the stored state, including any
 * generated id and audit metadata.
 */
public interface SaveOrderPort {

    Order save(Order order);
}
