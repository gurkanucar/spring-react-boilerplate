package com.gucardev.springreactboilerplate.domain.user.entity;

import com.gucardev.springreactboilerplate.domain.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "roles")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(length = 100)
    private String displayName;

    @Column(length = 255)
    private String description;
}
