package com.gucardev.springreactboilerplate.features.outbox.relay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * The outbox relay: on a fixed delay it drains due {@code PENDING} rows and publishes them to the
 * broker. This is the "asynchronously deliver" half of the transactional outbox pattern — decoupled
 * from the request transaction that wrote the rows.
 *
 * <p>Guarded by {@code @SchedulerLock} so that across multiple application instances exactly ONE
 * relay runs at a time (ShedLock; same mechanism the cleanup jobs use). That keeps two nodes from
 * publishing the same row without any row-level locking. If you need higher drain throughput, remove
 * this lock and switch the repository fetch to {@code FOR UPDATE SKIP LOCKED} so every instance can
 * claim a disjoint batch concurrently — see {@code OutboxMessageRepository#findPublishableIds}.
 *
 * <p>{@code lockAtMostFor} is a safety net: if this node dies mid-drain the lock frees after the
 * window so another instance can take over.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayJob {

    private final OutboxDispatcher dispatcher;

    @Scheduled(fixedDelayString = "${outbox.poll-delay-ms:2000}")
    @SchedulerLock(name = "OutboxRelayJob_dispatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1S")
    public void relay() {
        try {
            dispatcher.dispatchPending();
        } catch (Exception e) {
            // Never let a poll blow up the scheduler thread; the next tick retries.
            log.error("[OUTBOX] relay pass failed", e);
        }
    }
}
