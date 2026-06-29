package com.gucardev.springreactboilerplate.features.order.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link OrderJpaEntity}. An implementation detail of the persistence
 * adapter — the application core never sees it, only the {@code LoadOrderPort}/{@code SaveOrderPort}.
 */
@Repository
public interface OrderJpaRepository extends BaseJpaRepository<OrderJpaEntity, UUID> {
}
