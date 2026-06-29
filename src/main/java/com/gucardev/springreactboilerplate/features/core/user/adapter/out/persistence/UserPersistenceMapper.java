package com.gucardev.springreactboilerplate.features.core.user.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.role.adapter.out.persistence.RoleJpaEntity;
import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link User} domain model and the {@link UserJpaEntity}, including the audit
 * fields (via the super-builder) and the role association ({@link RoleJpaEntity} ⇄ domain
 * {@link Role}). Hand-written for clarity. The derived profile-image URL fields on the domain model
 * are presentation-only and intentionally not persisted.
 */
@Component
public class UserPersistenceMapper {

    User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .name(entity.getName())
                .surname(entity.getSurname())
                .phoneNumber(entity.getPhoneNumber())
                .profileImageId(entity.getProfileImageId())
                .organizationId(entity.getOrganizationId())
                .workspaceId(entity.getWorkspaceId())
                .activated(entity.getActivated())
                .isActive(entity.getIsActive())
                .roles(toDomainRoles(entity.getRoles()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    UserJpaEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserJpaEntity.builder()
                .id(user.getId())
                .password(user.getPassword())
                .email(user.getEmail())
                .name(user.getName())
                .surname(user.getSurname())
                .phoneNumber(user.getPhoneNumber())
                .profileImageId(user.getProfileImageId())
                .organizationId(user.getOrganizationId())
                .workspaceId(user.getWorkspaceId())
                .activated(user.getActivated())
                .isActive(user.getIsActive())
                .roles(toEntityRoles(user.getRoles()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    private Set<Role> toDomainRoles(Set<RoleJpaEntity> roles) {
        if (roles == null) {
            return new HashSet<>();
        }
        return roles.stream().map(this::toDomainRole).collect(Collectors.toCollection(HashSet::new));
    }

    private Role toDomainRole(RoleJpaEntity entity) {
        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private Set<RoleJpaEntity> toEntityRoles(Set<Role> roles) {
        if (roles == null) {
            return new HashSet<>();
        }
        return roles.stream().map(this::toEntityRole).collect(Collectors.toCollection(HashSet::new));
    }

    private RoleJpaEntity toEntityRole(Role role) {
        return RoleJpaEntity.builder()
                .id(role.getId())
                .name(role.getName())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .createdBy(role.getCreatedBy())
                .updatedBy(role.getUpdatedBy())
                .build();
    }
}
