package com.gucardev.springreactboilerplate.features.outbox.repository;

import com.gucardev.springreactboilerplate.features.outbox.entity.ProcessedMessage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Inbox / idempotency ledger. Plain {@link JpaRepository} (not {@code BaseJpaRepository}) because
 * {@link ProcessedMessage} is a bare marker row, not a {@code BaseEntity} with audit fields.
 */
@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, UUID> {
}
