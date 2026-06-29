package com.gucardev.springreactboilerplate.features.outbox.relay;

import com.gucardev.springreactboilerplate.features.outbox.config.OutboxProperties;
import com.gucardev.springreactboilerplate.features.outbox.entity.OutboxStatus;
import com.gucardev.springreactboilerplate.features.outbox.repository.OutboxMessageRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Reads one batch of due {@code PENDING} outbox rows and hands each to {@link OutboxMessageSender}
 * (which publishes it in its own {@code REQUIRES_NEW} transaction). The fetch returns just ids and
 * runs as a single read query — no long-lived transaction is held open across the broker I/O that
 * happens per row in the sender.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxDispatcher {

    private final OutboxMessageRepository repository;
    private final OutboxMessageSender sender;
    private final OutboxProperties properties;

    /** @return how many rows were attempted this pass (0 means the outbox was empty/idle). */
    public int dispatchPending() {
        List<UUID> ids = repository.findPublishableIds(
                OutboxStatus.PENDING, Instant.now(), PageRequest.of(0, properties.getBatchSize()));
        if (ids.isEmpty()) {
            return 0;
        }
        log.debug("[OUTBOX] dispatching {} pending message(s)", ids.size());
        for (UUID id : ids) {
            sender.send(id);
        }
        return ids.size();
    }
}
