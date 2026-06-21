package com.gucardev.springreactboilerplate.features.tenancy.organization.entity;

import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
 * A tenant (the top of the SaaS hierarchy). Workspaces belong to an organization; users are scoped
 * to one via {@code User.organizationId}. The {@code logoId} references a file in the store.
 */
@Entity
@Table(name = "organizations",
        uniqueConstraints = @UniqueConstraint(name = "uk_organizations_slug", columnNames = "slug"),
        indexes = @Index(name = "idx_organizations_created_at", columnList = "created_at"))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends BaseEntity {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String slug;

    @Column(length = 1000)
    private String description;

    @Column(length = 50)
    private String phoneNumber;

    @Column(length = 500)
    private String address;

    @Column(nullable = false)
    private Boolean isActive;

    /** Logo file id in the store; null if unset. Resolve its URL via {@code GET /files/{id}/url}. */
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "logo_id")
    private UUID logoId;
}
