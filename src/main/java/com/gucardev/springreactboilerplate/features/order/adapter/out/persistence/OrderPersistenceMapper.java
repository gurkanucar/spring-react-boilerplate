package com.gucardev.springreactboilerplate.features.order.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.order.domain.model.Order;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link Order} domain model and the {@link OrderJpaEntity}. Kept hand-written
 * (not MapStruct) because it spans the audit fields on {@code BaseEntity} via the super-builder and is
 * trivial enough to read at a glance.
 */
@Component
public class OrderPersistenceMapper {

    Order toDomain(OrderJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Order.fromPersistence(
                entity.getId(),
                entity.getCustomerName(),
                entity.getProduct(),
                entity.getQuantity(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy());
    }

    OrderJpaEntity toEntity(Order order) {
        if (order == null) {
            return null;
        }
        return OrderJpaEntity.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .product(order.getProduct())
                .quantity(order.getQuantity().value())
                .amount(order.getAmount().value())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .createdBy(order.getCreatedBy())
                .updatedBy(order.getUpdatedBy())
                .build();
    }
}
