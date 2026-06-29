package com.gucardev.springreactboilerplate.features.order.repository;

import com.gucardev.springreactboilerplate.features.order.entity.Order;
import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends BaseJpaRepository<Order, UUID> {
}
