package com.gucardev.springreactboilerplate.features.core.role.entity;

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
 * A grantable role. The {@code name} is stored without the {@code ROLE_} prefix (e.g.
 * {@code "ADMIN"}, {@code "USER"}); the prefix is added when authorities are built for Spring
 * Security (see {@code UserPrincipal}), so {@code @PreAuthorize("hasRole('ADMIN')")} matches.
 */
@Entity
@Table(name = "roles",
        uniqueConstraints = @UniqueConstraint(name = "uk_roles_name", columnNames = "name"))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

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
