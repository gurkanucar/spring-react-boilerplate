package com.gucardev.springreactboilerplate.features.core.role.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Persistence representation of a role — the driven-side JPA entity. It mirrors the
 * {@link com.gucardev.springreactboilerplate.features.core.role.domain.model.Role domain model} but
 * carries all the JPA mapping so the domain stays free of infrastructure. The persistence adapter
 * maps between the two.
 *
 * <p>The {@code name} is stored without the {@code ROLE_} prefix (e.g. {@code "ADMIN"},
 * {@code "USER"}); the prefix is added when authorities are built for Spring Security (see
 * {@code UserPrincipal}), so {@code @PreAuthorize("hasRole('ADMIN')")} matches.
 */
@Entity
@Table(name = "roles",
        uniqueConstraints = @UniqueConstraint(name = "uk_roles_name", columnNames = "name"))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RoleJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Uniqueness (and its backing index, used by findByName/existsByName) declared at table level.
    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 100)
    private String displayName;

    @Column(length = 255)
    private String description;
}
