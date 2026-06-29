package com.gucardev.springreactboilerplate.features.tenancy.organization.adapter.out.persistence;

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
 * Persistence representation of the organization (tenant) aggregate — the driven-side JPA entity. It
 * mirrors the {@link com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization
 * domain model} but carries all the JPA mapping so the domain stays free of infrastructure. The
 * persistence adapter maps between the two.
 *
 * <p>Organizations are the tenant root and are managed only by global super-admins (see
 * {@code OrganizationController}, where every endpoint is {@code hasRole('ADMIN')}).
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
public class OrganizationJpaEntity extends BaseEntity {

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
