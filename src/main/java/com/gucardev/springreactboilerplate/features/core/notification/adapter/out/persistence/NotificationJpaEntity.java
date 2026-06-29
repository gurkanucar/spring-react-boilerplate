package com.gucardev.springreactboilerplate.features.core.notification.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

/**
 * Persistence representation of the notification aggregate — the driven-side JPA entity. It mirrors the
 * {@link com.gucardev.springreactboilerplate.features.core.notification.domain.model.Notification
 * domain model} but carries all the JPA mapping so the domain stays free of infrastructure. The
 * persistence adapter maps between the two.
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notifications_recipient", columnList = "workspace_id, recipient_id"),
        @Index(name = "idx_notifications_read", columnList = "is_read"),
        @Index(name = "idx_notifications_created_at", columnList = "created_at")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationJpaEntity extends BaseEntity {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    /** The user this notification is for. */
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    /** Machine-readable category, e.g. NEWS_CREATED. */
    @Column(nullable = false, length = 60)
    private String type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean read;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
