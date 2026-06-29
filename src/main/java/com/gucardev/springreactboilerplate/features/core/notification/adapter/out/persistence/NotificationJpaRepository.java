package com.gucardev.springreactboilerplate.features.core.notification.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link NotificationJpaEntity}. An implementation detail of the persistence
 * adapter — the application core never sees it, only the notification output ports.
 */
@Repository
public interface NotificationJpaRepository extends BaseJpaRepository<NotificationJpaEntity, UUID> {

    Page<NotificationJpaEntity> findByWorkspaceIdAndRecipientId(UUID workspaceId, UUID recipientId, Pageable pageable);

    Page<NotificationJpaEntity> findByWorkspaceIdAndRecipientIdAndReadFalse(
            UUID workspaceId, UUID recipientId, Pageable pageable);

    long countByWorkspaceIdAndRecipientIdAndReadFalse(UUID workspaceId, UUID recipientId);

    @Modifying
    @Query("update NotificationJpaEntity n set n.read = true, n.readAt = :now "
            + "where n.workspaceId = :workspaceId and n.recipientId = :recipientId and n.read = false")
    int markAllRead(@Param("workspaceId") UUID workspaceId,
                    @Param("recipientId") UUID recipientId,
                    @Param("now") LocalDateTime now);

    /** Remove every notification for a workspace — used when the workspace is deleted. */
    @Modifying
    @Query("delete from NotificationJpaEntity n where n.workspaceId = :workspaceId")
    int deleteByWorkspaceId(@Param("workspaceId") UUID workspaceId);
}
