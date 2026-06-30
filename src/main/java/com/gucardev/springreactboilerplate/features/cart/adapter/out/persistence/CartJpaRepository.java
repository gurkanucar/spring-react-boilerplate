package com.gucardev.springreactboilerplate.features.cart.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link CartJpaEntity}. An implementation detail of the persistence
 * adapter — the application core never sees it, only the {@code LoadCartPort}/{@code SaveCartPort}.
 */
@Repository
public interface CartJpaRepository extends BaseJpaRepository<CartJpaEntity, UUID> {
}
