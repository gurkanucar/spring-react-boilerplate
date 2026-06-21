package com.gucardev.springreactboilerplate.features.core.notification.repository;

import com.gucardev.springreactboilerplate.features.core.notification.entity.Notification;
import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends BaseJpaRepository<Notification, UUID> {

    Page<Notification> findByWorkspaceIdAndRecipientId(UUID workspaceId, UUID recipientId, Pageable pageable);

    Page<Notification> findByWorkspaceIdAndRecipientIdAndReadFalse(
            UUID workspaceId, UUID recipientId, Pageable pageable);

    long countByWorkspaceIdAndRecipientIdAndReadFalse(UUID workspaceId, UUID recipientId);

    @Modifying
    @Query("update Notification n set n.read = true, n.readAt = :now "
            + "where n.workspaceId = :workspaceId and n.recipientId = :recipientId and n.read = false")
    int markAllRead(@Param("workspaceId") UUID workspaceId,
                    @Param("recipientId") UUID recipientId,
                    @Param("now") LocalDateTime now);

    /** Remove every notification for a workspace — used when the workspace is deleted. */
    @Modifying
    @Query("delete from Notification n where n.workspaceId = :workspaceId")
    int deleteByWorkspaceId(@Param("workspaceId") UUID workspaceId);
}
