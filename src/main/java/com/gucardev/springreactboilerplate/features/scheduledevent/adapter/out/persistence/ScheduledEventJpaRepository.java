package com.gucardev.springreactboilerplate.features.scheduledevent.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link ScheduledEventJpaEntity}. An implementation detail of the
 * persistence adapter — the application core never sees it, only the load/save/search output ports.
 */
@Repository
public interface ScheduledEventJpaRepository extends BaseJpaRepository<ScheduledEventJpaEntity, UUID> {
}
