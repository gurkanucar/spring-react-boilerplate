package com.gucardev.springreactboilerplate.features.scheduledevent.repository;

import com.gucardev.springreactboilerplate.features.scheduledevent.entity.ScheduledEvent;
import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledEventRepository extends BaseJpaRepository<ScheduledEvent, UUID> {
}
