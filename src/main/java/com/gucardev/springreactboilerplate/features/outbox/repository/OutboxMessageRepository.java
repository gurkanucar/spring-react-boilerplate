package com.gucardev.springreactboilerplate.features.outbox.repository;

import com.gucardev.springreactboilerplate.features.outbox.entity.OutboxMessage;
import com.gucardev.springreactboilerplate.features.outbox.entity.OutboxStatus;
import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxMessageRepository extends BaseJpaRepository<OutboxMessage, UUID> {

    /**
     * Ids of rows the relay should publish now: still {@code PENDING} and whose backoff window has
     * elapsed ({@code nextAttemptAt} null or due), oldest first to preserve rough FIFO order.
     *
     * <p>Only one relay runs at a time (guarded by ShedLock — see {@code OutboxRelayJob}), so no
     * row-level locking is needed here. If you instead want several instances draining the outbox in
     * parallel, drop the ShedLock guard and change this to a locking fetch with
     * {@code SELECT ... FOR UPDATE SKIP LOCKED} (PostgreSQL) so each poller claims a disjoint batch.
     */
    @Query("""
            select m.id from OutboxMessage m
            where m.status = :status
              and (m.nextAttemptAt is null or m.nextAttemptAt <= :now)
            order by m.createdAt asc
            """)
    List<UUID> findPublishableIds(@Param("status") OutboxStatus status,
                                  @Param("now") Instant now,
                                  Pageable pageable);
}
